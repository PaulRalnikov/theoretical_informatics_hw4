import java.io.File
import kotlin.math.max

class DFA(val n : Int = 0,
          val m : Int = 0,
          val start : Int = 0,
          val finish : Set<Int> = emptySet(),
          val go : Array<Array<Int>> = emptyArray()
) {

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

        fun compress()  {
            TODO()
        }
    }

    fun minimize() : DFA {
        val category = Category(Array(n) {
            if (it in finish) 1 else 0
        })
        var flag = true
        while (flag) {
            flag = false
            for (type in category.typeContent.withIndex()) {
                var deletedType = false
                for (letter in 0 until m) {
                    val outgoingTypes : Set<Int> = type.value.map {v -> category.getType(go[v][letter])}.toSet()
                    if (outgoingTypes.size == 1) continue
                    deletedType = true

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

fun readDfa(inputFile : File) : DFA {
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
