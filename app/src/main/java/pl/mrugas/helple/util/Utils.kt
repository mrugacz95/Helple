package pl.mrugas.helple.util

fun <T : Enum<T>> T.next(): T {
    val values = declaringClass.enumConstants!!
    val nextOrdinal = (ordinal + 1) % values.size
    return values[nextOrdinal]
}
