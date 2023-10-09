import java.io.File
import kotlin.test.Test

internal class TestMinimize {
    private val testData = "src/test/data/"
    private val inputFilename = "input.txt"
    private val expectedOutputFilename = "expected-output.txt"

    @Test
    fun test_1() {
        val testDir = "test1/"
        val inputPath = testData + testDir + inputFilename
        val expectedOutputPath = testData + testDir + expectedOutputFilename

        val dfa = DFA.read(File(inputPath))
        val minimizedDfa = DFA.getMinimised(dfa)
        val rightDfa = DFA.read(File(expectedOutputPath))

        assert(minimizedDfa.equalOther(rightDfa))
    }

    @Test
    fun test_2() {
        val testDir = "test2/"
        val inputPath = testData + testDir + inputFilename
        val expectedOutputPath = testData + testDir + expectedOutputFilename

        val dfa = DFA.read(File(inputPath))
        val minimizedDfa = DFA.getMinimised(dfa)
        val rightDfa = DFA.read(File(expectedOutputPath))

        assert(minimizedDfa.equalOther(rightDfa))
    }

    @Test
    fun test_3() {
        val testDir = "test3/"
        val inputPath = testData + testDir + inputFilename
        val expectedOutputPath = testData + testDir + expectedOutputFilename

        val dfa = DFA.read(File(inputPath))
        val minimizedDfa = DFA.getMinimised(dfa)
        val rightDfa = DFA.read(File(expectedOutputPath))



        assert(minimizedDfa.equalOther(rightDfa))
    }
}