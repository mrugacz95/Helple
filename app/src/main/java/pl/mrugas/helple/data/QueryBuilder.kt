package pl.mrugas.helple.data

import androidx.sqlite.db.SimpleSQLiteQuery

data class QueryBuilder(
    private val columns: String = "*",
    private val tables: String = "words",
    private val whereClause: StringBuilder = StringBuilder(),
    private var count: Boolean = false
) {

    fun addKnownLetter(letter: Char, position: Int): QueryBuilder = apply {
        appendWhere("letter${position} is \"${letter}\"")
        return this
    }

    fun addWrongLetter(letter: Char, position: Int) = apply {
        appendWhere("letter${position} is not \"${letter}\"")
    }

    fun addIncorrectPlaceLetter(letter: Char, position: Int, wordLen: Int) = apply {
        appendWhere("letter${position} is not \"${letter}\"")
        appendOrGroup(*((0 until wordLen)
            .filter { it != position }
            .map { pos ->
                "letter$pos is \"${letter}\""
            })
            .toTypedArray()
        )
    }

    fun setWordsLen(wordLength: Int) = apply {
        appendWhere("length is $wordLength")
    }

    private fun appendOrGroup(vararg inWhere: String) = apply {
        if (whereClause.isNotEmpty()) {
            whereClause.append(" AND ")
        }
        whereClause.append(inWhere.joinToString(prefix = "(", separator = " OR ", postfix = ") "))
    }

    private fun appendWhere(inWhere: CharSequence) = apply {
        if (whereClause.isNotEmpty()) {
            whereClause.append(" AND ")
        }
        whereClause.append('(').append(inWhere).append(')')
    }

    fun count() = apply {
        count = true
    }

    fun build(): SimpleSQLiteQuery {
        return SimpleSQLiteQuery(toString())
    }

    override fun toString(): String {
        val query = StringBuilder()
        query.append("SELECT ")
        if (count) {
            query.append("COUNT(*)")
        } else {
            query.append(columns)
        }
        query.append(" FROM ")
        query.append(tables)
        query.append(" WHERE ")
        query.append(whereClause)
        return query.toString()
    }

    fun copy(): QueryBuilder {
        return QueryBuilder(
            columns = columns,
            tables = tables,
            whereClause = StringBuilder(whereClause),
            count = count
        )
    }
}
