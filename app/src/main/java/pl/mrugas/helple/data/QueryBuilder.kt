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

    private fun addKnownLetter(letter: Char, position: Int): QueryBuilder = apply {
        appendWhere("letter${position} is '${letter}'")
        return this
    }

    private fun addWrongLetter(letter: Char, position: Int) = apply {
        appendWhere("letter${position} is not '${letter}'")
    }

    private fun excludeLetter(letter: Char, wordLen: Int) = apply {
        for (position in 0 until wordLen) {
            appendWhere("letter${position} is not '${letter}'")
        }
    }

    private fun addIncorrectPlaceLetter(letter: Char, position: Int, wordLen: Int) = apply {
        appendWhere("letter${position} is not '${letter}'")
        appendOrGroup(*((0 until wordLen)
            .filter { it != position }
            .map { pos ->
                "letter$pos is '${letter}'"
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
        if (whereClause.isNotEmpty()) {
            query.append(" WHERE ")
            query.append(whereClause)
        }
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
                            tile.state == TileState.INCORRECT_PLACE &&
                            tile.letter == letter
                    }
                ) {
                    query.addWrongLetter(letter, position)
                } else if (wordState.tiles.any { tile ->
                        tile.id != position &&
                            tile.state == TileState.CORRECT_PLACE &&
                            tile.letter == letter
                    }
                ) {
                    for (tile in wordState.tiles) {
                        if (tile.state != TileState.CORRECT_PLACE) {
                            query.addWrongLetter(letter, tile.id)
                        }
                    }
                } else {
                    query.excludeLetter(letter, wordLen)
                }
            }
        }
    }

    companion object {
        fun fromGameState(
            gameState: GameState,
            correctLettersOption: LetterOption = LetterOption.INCLUDE,
            incorrectPlaceLetterOption: LetterOption = LetterOption.INCLUDE,
            wrongLetterOption: LetterOption = LetterOption.INCLUDE
        ): QueryBuilder {
            val query = QueryBuilder()
            for (word in gameState.words) {
                for (tile in word.tiles) {
                    val option = when (tile.state) {
                        TileState.CORRECT_PLACE -> correctLettersOption
                        TileState.INCORRECT_PLACE -> incorrectPlaceLetterOption
                        TileState.WRONG -> wrongLetterOption
                    }
                    when (option) {
                        LetterOption.INCLUDE -> query.appendTileRuleToQuery(
                            state = tile.state,
                            wordState = word,
                            letter = tile.letter,
                            position = tile.id,
                            wordLen = gameState.wordLen,
                            query = query
                        )
                        LetterOption.EXCLUDE -> query.addWrongLetter(tile.letter, tile.id)
                        LetterOption.OMIT -> continue
                    }
                }
            }
            query.setWordsLen(gameState.wordLen)
            return query
        }
    }
}

enum class LetterOption { INCLUDE, EXCLUDE, OMIT }
