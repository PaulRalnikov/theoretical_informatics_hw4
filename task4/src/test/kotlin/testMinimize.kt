import java.io.File
import kotlin.test.Test

internal class TestMinimize {
    private val testData = "src/test/data/"
    private val inputFilename = "input.txt"
    private val expectedOutputFilename = "expected-output.txt"

    @Test
    fun test_1() {
        val inputPath = testData + "test1/" + inputFilename
        val expectedOutputPath = testData + "test1/" + expectedOutputFilename
//        val outputPath = kotlin.io.path.createTempFile().toString()
        val outputPath = testData + "test1/output.txt"

        val dfa = DFA.read(File(inputPath))
        val minimizedDfa = DFA.getMinimised(dfa)

        minimizedDfa.printToFile(File(outputPath))

        val rightDfa = DFA.read(File(expectedOutputPath))

        assert(minimizedDfa.equalOther(rightDfa))
    }
}