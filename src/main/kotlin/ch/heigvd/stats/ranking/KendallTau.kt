package ch.heigvd.stats.ranking

import kotlin.coroutines.experimental.buildSequence
import kotlin.math.sqrt

/**
 * Rough implementation of Kendall-Tau correlation coefficient computation.
 *
 * This implementation:
 *
 * - is slow (O(n²)), thanks to efficient sorts a smarter implementation should be O(n·log(n))
 * - doesn't handle cases based on non-square contingency tables (Tau-c)
 * - simple and easy to understand
 *
 */
interface KendallTau {

    /**
     * Kendall-Tau-a
     *
     * This variant ignores ties
     *
     * More details on Wikipedia (https://en.wikipedia.org/wiki/Kendall_rank_correlation_coefficient#Tau-a)
     *
     * @return the Tau-a value
     */
    fun tauA(): Double

    /**
     * Kendall-Tau-b
     *
     * This variant supports ties
     *
     * More details on Wikipedia (https://en.wikipedia.org/wiki/Kendall_rank_correlation_coefficient#Tau-b)
     *
     * @return the Tau-b value
     */
    fun tauB(): Double

    companion object {

        /**
         * build a [KendallTau] instance with two rankings
         *
         * @return a new instance
         */
        fun <T, U> build(a: Array<T>, b: Array<U>): KendallTau where T : Comparable<T>, U : Comparable<U> {

            (a.size == b.size) || throw Exception("Arrays need to have equals sizes")

            return object : KendallTau {

                /**
                 * returns a list of available indices pairs
                 *
                 * Only pairs, st that P=(a,b) with a>b are returned in the list
                 */
                private fun indices() = buildSequence {
                    for (i in a.indices)
                        for (j in 0 until i)
                            yield(Pair(i, j))
                }

                /**
                 * pairs comparison values
                 */
                private val pairs: Sequence<Int> by lazy {
                    indices().map { a[it.first].compareTo(a[it.second]) * b[it.first].compareTo(b[it.second]) }
                }

                /**
                 * list of numbers of tied values in the i-th group of ties for the first quantity
                 */
                private val t by lazy {
                    a.indices.map { Pair(a[it], it) }.groupBy { it.first }.map { it.value.size }
                }

                /**
                 * list of numbers of tied values in the i-th group of ties for the second quantity
                 */
                private val u by lazy {
                    b.indices.map { Pair(b[it], it) }.groupBy { it.first }.map { it.value.size }
                }

                /**
                 * number of concordant pairs
                 */
                private val nc by lazy {
                    pairs.filter { it > 0 }.count()
                }

                /**
                 * number of discordant pairs
                 */
                private val nd by lazy {
                    pairs.filter { it < 0 }.count()
                }

                private val n0 by lazy {
                    a.size * (a.size - 1) / 2
                }

                private val n1 by lazy {
                    t.map { it * (it - 1) }.sum() / 2
                }

                private val n2 by lazy {
                    u.map { it * (it - 1) }.sum() / 2
                }

                override fun tauA() = 1.0 * (nc - nd) / n0

                override fun tauB() = (nc - nd) / sqrt(1.0 * (n0 - n1) * (n0 - n2))
            }
        }
    }
}
