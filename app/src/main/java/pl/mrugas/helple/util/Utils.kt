package pl.mrugas.helple.util

fun <T : Enum<T>> T.next(omit: T? = null): T {
    val values = declaringClass.enumConstants!!
    val nextOrdinal = (ordinal + 1) % values.size
    return if (values[nextOrdinal] != omit) {
        values[nextOrdinal]
    } else {
        next()
    }
}
