package pl.mrugas.helple.data

import androidx.sqlite.db.SimpleSQLiteQuery
import pl.mrugas.helple.ui.GameState
import pl.mrugas.helple.ui.TileState
import pl.mrugas.helple.ui.WordState

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

    fun excludeLetter(letter: Char, wordLen: Int) = apply {
        for (position in 0 until wordLen) {
            appendWhere("letter${position} is not \"${letter}\"")
        }
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

    fun appendTileRuleToQuery(
        state: TileState,
        wordState: WordState,
        letter: Char,
        position: Int,
        wordLen: Int,
        query: QueryBuilder
    ) {
        when (state) {
            TileState.CORRECT_PLACE -> query.addKnownLetter(letter, position)
            TileState.INCORRECT_PLACE -> query.addIncorrectPlaceLetter(letter, position, wordLen)
            TileState.WRONG -> {
                if (wordState.tiles.any { tile ->
                        tile.id != position &&
                            tile.state != TileState.WRONG &&
                            tile.letter == letter
                    }
                ) {
                    query.addWrongLetter(letter, position)
                } else {
                    query.excludeLetter(letter, wordLen)
                }
            }
        }
    }

    companion object {
        fun fromGameState(gameState: GameState): QueryBuilder {
            val query = QueryBuilder()
            for (word in gameState.words) {
                for (tile in word.tiles) {
                    query.appendTileRuleToQuery(tile.state, word, tile.letter, tile.id, gameState.wordLen, query)
                }
            }
            query.setWordsLen(gameState.wordLen)
            return query
        }
    }
}
