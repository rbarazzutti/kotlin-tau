package ch.heigvd.stats.ranking


import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Fail.fail
import org.junit.jupiter.api.Test
import kotlin.math.sqrt

class KendallTauTest {
    @Test
    fun perfectOrder() {
        val kt = KendallTau.build(arrayOf(1, 2, 3), arrayOf('a', 'b', 'c'))

        assertThat(kt.tauA()).isEqualTo(1.0)

        assertThat(kt.tauB()).isEqualTo(1.0)
    }

    @Test
    fun perfectDisorder() {
        val kt = KendallTau.build(arrayOf(1, 2, 3), arrayOf('c', 'b', 'a'))

        assertThat(kt.tauA()).isEqualTo(-1.0)

        assertThat(kt.tauB()).isEqualTo(-1.0)
    }

    @Test
    fun example1() {
        // http://www.statisticshowto.com/kendalls-tau/
        val kt = KendallTau.build(arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12), arrayOf(1, 2, 4, 3, 6, 5, 8, 7, 10, 9, 12, 11))

        assertThat(kt.tauA()).isEqualTo(56.0 / 66)

        assertThat(kt.tauB()).isEqualTo(56.0 / 66)
    }

    @Test
    fun example2() {
        // https://www.utdallas.edu/~herve/Abdi-KendallCorrelation2007-pretty.pdf
        val kt = KendallTau.build(arrayOf(1, 3, 2, 4), arrayOf(1, 4, 2, 3))

        assertThat(kt.tauA()).isEqualTo(2.0 / 3)

        assertThat(kt.tauB()).isEqualTo(2.0 / 3)
    }

    @Test
    fun example3() {
        // https://stats.stackexchange.com/questions/261206/kendall-s-tau-b-version-calculation-steps-with-tied-ranks
        val kt = KendallTau.build(arrayOf(12, 2, 1, 12, 2), arrayOf(1, 4, 7, 1, 0))

        assertThat(kt.tauB()).isEqualTo(-4.0 / sqrt(72.0))
    }

    @Test
    fun exceptionWithDifferentSizes() {
        try {
            KendallTau.build(arrayOf(1, 2, 3), arrayOf('d', 'c', 'b', 'a'))
            fail("List of different sizes not supported")
        } catch (ex: Exception) {
            // do nothing
        }
    }
}