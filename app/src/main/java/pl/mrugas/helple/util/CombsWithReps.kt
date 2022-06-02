package pl.mrugas.helple.util

class CombsWithReps<T>(val m: Int, val n: Int, val items: List<T>) {
    private val combination = IntArray(m)
    private var count = 0

    fun generate(): Sequence<List<T>> {
        return generate(0)
    }

    private fun generate(k: Int): Sequence<List<T>> {
        return sequence {
            if (k >= m) {
                count++
                yield((0 until m).map { items[combination[it]] })
            } else {
                for (j in 0 until n)
                    if (k == 0 || j >= combination[k - 1]) {
                        combination[k] = j
                        yieldAll(generate(k + 1))
                    }
            }
        }
    }
}
