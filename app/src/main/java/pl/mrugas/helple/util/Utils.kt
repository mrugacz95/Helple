package pl.mrugas.helple.util

fun <T : Enum<T>> T.next(omit: T? = null): T {
    val values = declaringJavaClass.enumConstants!!
    val nextOrdinal = (ordinal + 1) % values.size
    return if (values[nextOrdinal] != omit) {
        values[nextOrdinal]
    } else {
        next()
    }
}


fun flattenList(nestList: List<Any>): List<Any> {
    val flatList = mutableListOf<Any>()

    fun flatten(list: List<Any>) {
        for (e in list) {
            if (e !is List<*>)
                flatList.add(e)
            else
                @Suppress("UNCHECKED_CAST")
                flatten(e as List<Any>)
        }
    }

    flatten(nestList)
    return flatList
}

operator fun List<Any>.times(other: List<Any>): List<List<Any>> {
    val prod = mutableListOf<List<Any>>()
    for (e in this) {
        for (f in other) {
            prod.add(listOf(e, f))
        }
    }
    return prod
}

fun <T> nAryCartesianProduct(lists: List<List<Any>>): List<List<T>> {
    require(lists.size >= 2)
    @Suppress("UNCHECKED_CAST")
    return lists.drop(2).fold(lists[0] * lists[1]) { cp, ls -> cp * ls }.map { flattenList(it) } as List<List<T>>
}
