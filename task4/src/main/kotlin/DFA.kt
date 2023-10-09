import java.io.File
import java.util.*
import kotlin.math.max

class DFA(val n : Int = 0,
          val m : Int = 0,
          val start : Int = 0,
          val finish : Set<Int> = emptySet(),
          val go : Array<Array<Int>> = emptyArray()
) {

    companion object {
        fun getMinimised(dfa : DFA) = dfa.deleteUnreachableStates().minimize()

        fun read(inputFile : File) : DFA {
            inputFile.bufferedReader().use {reader ->
                val n = reader.readLine().toInt()
                val m = reader.readLine().toInt()

                val start = reader.readLine().toInt()
                val finish = reader.readLine().split(" ").map {it.toInt()}.toSet()

                val go = Array(n) {
                    Array(m) { 0 }
                }

                while(true) {
                    try {
                        val splitted: List<Int> = reader.readLine().split(" ").map {it.toInt()}
                        val from : Int = splitted[0]
                        val symbol : Int = splitted[1]
                        val to : Int = splitted[2]
                        go[from][symbol] = to
                    } catch (e : Exception) {
                        break
                    }
                }
                return DFA(n, m, start, finish, go)
            }
        }
    }

    inner class Category (val type : Array<Int>) {
        val typeContent : MutableList<MutableList<Int>>
        var nextFreeType : Int
        val freeTypes : MutableList<Int>
        init {
            typeContent = MutableList (n) { mutableListOf()}
            nextFreeType = 0
            freeTypes = mutableListOf()

            for (i in 0 until n) {
                typeContent[type[i]].add(i)
                nextFreeType = max(nextFreeType, type[i] + 1)
            }

            for (i in 0 until nextFreeType) {
                if (typeContent[i].isEmpty()) freeTypes.add(i)
            }
        }

        fun addType(content : List<Int>) {
            val numType : Int
            if (freeTypes.isNotEmpty()) numType = freeTypes.removeLast()
            else {
                numType = nextFreeType++
                typeContent.add(mutableListOf())
            }
            typeContent[numType] = content.toMutableList()
            content.forEach { type[it] = numType }
        }

        fun eraseType(numType : Int) {
            typeContent[numType].forEach { type[it] = -1 }
            typeContent[numType] = mutableListOf()
            freeTypes.add(numType)
        }

        fun getType(v : Int) = type[v]


        //перенумеровывает состояния в последовательность 1..n
        fun compress()  {
            //переименовывает состояние oldNum в newNum
            fun renameType(oldNum : Int, newNum : Int) {
                typeContent[oldNum].forEach { type[it] = newNum }
                typeContent[newNum] = typeContent[oldNum]
                typeContent[oldNum] = mutableListOf()
            }

            var iFree = 0
            val newNextFreeType = typeContent.count { it.isNotEmpty() }
            for (i in 0 until nextFreeType) {
                while (iFree < i && typeContent[iFree].isNotEmpty()) ++iFree
                if (typeContent[i].isEmpty() || i == iFree) continue
                renameType(i, iFree)
            }
            nextFreeType = newNextFreeType
        }

        fun print() {
            println("typeContent:")
            for (t in 0 until nextFreeType) {
                if (typeContent[t].isNotEmpty()) {
                    println("type $t:")
                    println(typeContent[t])
                }
            }
            println("=====================")
        }
    }


    //работает только с автоматами, где все состояния достижимы
    private fun minimize() : DFA {

        val dfa = deleteUnreachableStates()
        val category = Category(Array(dfa.n) {
            if (it in dfa.finish) 1 else 0
        })
//        println("start types:")
//        category.print()
        var flag = true
        while (flag) {
            flag = false
            for (type in category.typeContent.withIndex()) {
                if (type.value.isEmpty()) continue
                var deletedType = false
                for (letter in 0 until m) {
                    val outgoingTypes : Set<Int> = type.value.map {v -> category.getType(go[v][letter])}.toSet()
                    if (outgoingTypes.size == 1) continue
                    deletedType = true
//                    println("find type for split ${type.value} by letter $letter")
//                    println("outgoingTypes: $outgoingTypes")
//                    type.value.forEach {v -> println("go from vertex $v letter $letter to vertex ${go[v][letter]} type ${category.getType(go[v][letter])}") }

                    //numeratedOutgoingTypes says number of new class by type of the vertex
                    val numeratedOutgoingTypes : Map<Int, Int> = outgoingTypes.mapIndexed {idx, it -> it to idx}.toMap()
                    val countNewTypes = numeratedOutgoingTypes.size
                    val newTypes = List<MutableList<Int>> (countNewTypes) { mutableListOf()}

                    type.value.forEach {v ->
                        newTypes[
                            numeratedOutgoingTypes.getOrDefault(
                                category.getType(go[v][letter]),
                                -1)
                        ].add(v)
                    }

                    category.eraseType(type.index)
                    newTypes.forEach { category.addType(it) }
                    break
                }
                if (deletedType) {
                    flag = true
                    break
                }
            }
//            if (flag) {
//                println("new configuration:")
//                category.print()
//            }
        }
        category.compress()
        val newN = category.nextFreeType
        val newM = m
        val newStart = category.getType(start)
        val newFinish = finish.map { category.getType(it) }.toSet()
        val newGo = Array(newN) { v ->
            Array (newM) {letter ->
                category.getType(
                    go[category.typeContent[v][0]][letter]
                )
            }
        }
        return DFA(newN, newM, newStart, newFinish, newGo)
    }

    fun equalOther (dfa : DFA) : Boolean {
        if (n != dfa.n || m != dfa.m) return false
        if (finish.count() != dfa.finish.count()) return false
        val accordance = mutableMapOf<Int, Int>()
        accordance[start] = dfa.start
        val queue : Queue<Int> = LinkedList()
        queue.add(start)
        val used = mutableSetOf<Int>()
        while (queue.isNotEmpty()) {
            val v = queue.poll().toInt()
            if (used.contains(v)) continue
            used.add(v)
            val u = accordance.getOrDefault(v, -1)
            for (letter in 0 until m) {
                if (used.contains(go[v][letter])) continue
                if (!accordance.containsKey(go[v][letter])) {
                    accordance[go[v][letter]] = go[u][letter]
                    queue.add(go[v][letter])
                }
                if (accordance.getOrDefault(go[v][letter], -1) != go[u][letter])
                    return false
            }
        }
        return true
    }

    fun deleteUnreachableStates() : DFA {
        var newN = 0
        val newM = m
        val newStart = 0
        val newFinish = mutableSetOf<Int>()
        val newGo : MutableList<Array<Int> > = mutableListOf()

        val newIndex = mutableMapOf<Int, Int>()
        val queue : Queue<Int> = LinkedList()
        val used = mutableSetOf<Int>()

        newIndex[start] = newN++
        newGo.add(Array(newM) {-1})

        queue.add(start)

        while (queue.isNotEmpty()) {
            val v = queue.poll().toInt()

            if (used.contains(v)) continue
            used.add(v)

            val newV = newIndex.getOrDefault(v, -1)
            if (finish.contains(v)) newFinish.add(newV)

            for (letter in 0 until m) {
                val to = go[v][letter]

                if (!newIndex.containsKey(to)) {
                    newIndex[to] = newN++
                    newGo.add(Array(newM) {-1})
                }

                val newTo = newIndex.getOrDefault(to, -1)
                newGo[newV][letter] = newTo

                if (!used.contains(to)) {
                    queue.add(to)
                }
            }
        }
        return DFA(
            newN,
            newM,
            newStart,
            newFinish,
            newGo.toTypedArray()
        )
    }

    fun printToFile(outputFile : File) {
        if (!outputFile.exists()) outputFile.createNewFile()
        outputFile.printWriter().use {
            it.println(n)
            it.println(m)
            it.println(start)
            it.println(finish.toSortedSet().joinToString(" "))
            for (i in 0 until n) {
                for (c in 0 until m) {
                    it.println("$i $c ${go[i][c]}")
                }
            }
        }
    }
}

