import java.io.File

fun main() {
    println("enter relative path (from directory with project) to file with input data:")
    val inputPath = readlnOrNull().toString()
    println("enter relative path to result file:")
    val resultPath = readlnOrNull().toString()
    val dfa = readDfa(File(inputPath))
    dfa.minimize().printToFile(File(resultPath))
}