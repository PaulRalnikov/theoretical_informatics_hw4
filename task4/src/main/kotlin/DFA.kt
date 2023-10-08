import java.io.File

class DFA(val n : Int = 0,
          val m : Int = 0,
          val start : Int = 0,
          val finish : Set<Int> = emptySet(),
          val go : Array<Array<Int>> = emptyArray()
) {

    fun minimize() : DFA {

        return DFA()
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
