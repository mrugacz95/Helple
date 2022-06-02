package pl.mrugas.helple.domain

import pl.mrugas.helple.data.DbWord
import pl.mrugas.helple.data.QueryBuilder
import pl.mrugas.helple.data.WordDao
import pl.mrugas.helple.ui.GameState
import pl.mrugas.helple.ui.TileState
import pl.mrugas.helple.ui.WordState
import kotlin.math.max

class MinimaxSolver : Solver {
    override suspend fun guessNewWord(
        gameState: GameState,
        wordDao: WordDao,
        updateProgress: (progress: Float) -> Unit
    ): DbWord? {
        val query = QueryBuilder.fromGameState(gameState)
        val possibleWords = wordDao.rawQuery(query.build())
        val possibleHints = generatePossibleHints(gameState.wordLen)
        return possibleWords
            .withIndex()
            .minByOrNull { (idx, word) ->
                val worstResult = countWorstResult(gameState, wordDao, word.toString(), possibleHints)
                updateProgress(idx.toFloat() / possibleWords.size)
                worstResult
            }?.value
    }

    private suspend fun countWorstResult(
        gameState: GameState,
        wordDao: WordDao,
        word: String,
        possibleHints: List<List<TileState>>
    ): Int {
        var worst = 0
        val query = QueryBuilder.fromGameState(gameState).count()
        for (hint in possibleHints) {
            val queryCopy = query.copy()
            val wordState = WordState.fromWordAndStates(word, hint)
            for (tile in wordState.tiles) {
                queryCopy.appendTileRuleToQuery(tile.state, wordState, tile.letter, tile.id, word.length, queryCopy)
            }
            val result = wordDao.rawCountQuery(queryCopy.build())
            worst = max(worst, result)
        }
        return worst
    }

    @Suppress("UnstableApiUsage")
    private fun generatePossibleHints(wordLen: Int): List<List<TileState>> {
        // TODO change to generating from hardcoded list
        return listOf(
            listOf(
                TileState.CORRECT_PLACE,
                TileState.CORRECT_PLACE,
                TileState.CORRECT_PLACE,
                TileState.CORRECT_PLACE,
                TileState.INCORRECT_PLACE
            ),
            listOf(
                TileState.CORRECT_PLACE,
                TileState.CORRECT_PLACE,
                TileState.CORRECT_PLACE,
                TileState.CORRECT_PLACE,
                TileState.WRONG
            ),
            listOf(
                TileState.CORRECT_PLACE,
                TileState.CORRECT_PLACE,
                TileState.CORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.INCORRECT_PLACE
            ),
            listOf(
                TileState.CORRECT_PLACE,
                TileState.CORRECT_PLACE,
                TileState.CORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.WRONG
            ),
            listOf(
                TileState.CORRECT_PLACE,
                TileState.CORRECT_PLACE,
                TileState.CORRECT_PLACE,
                TileState.WRONG,
                TileState.WRONG
            ),
            listOf(
                TileState.CORRECT_PLACE,
                TileState.CORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.INCORRECT_PLACE
            ),
            listOf(
                TileState.CORRECT_PLACE,
                TileState.CORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.WRONG
            ),
            listOf(
                TileState.CORRECT_PLACE,
                TileState.CORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.WRONG,
                TileState.WRONG
            ),
            listOf(
                TileState.CORRECT_PLACE,
                TileState.CORRECT_PLACE,
                TileState.WRONG,
                TileState.WRONG,
                TileState.WRONG
            ),
            listOf(
                TileState.CORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.INCORRECT_PLACE
            ),
            listOf(
                TileState.CORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.WRONG
            ),
            listOf(
                TileState.CORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.WRONG,
                TileState.WRONG
            ),
            listOf(
                TileState.CORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.WRONG,
                TileState.WRONG,
                TileState.WRONG
            ),
            listOf(
                TileState.CORRECT_PLACE,
                TileState.WRONG,
                TileState.WRONG,
                TileState.WRONG,
                TileState.WRONG
            ),
            listOf(
                TileState.INCORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.INCORRECT_PLACE
            ),
            listOf(
                TileState.INCORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.WRONG
            ),
            listOf(
                TileState.INCORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.WRONG,
                TileState.WRONG
            ),
            listOf(
                TileState.INCORRECT_PLACE,
                TileState.INCORRECT_PLACE,
                TileState.WRONG,
                TileState.WRONG,
                TileState.WRONG
            ),
            listOf(
                TileState.INCORRECT_PLACE,
                TileState.WRONG,
                TileState.WRONG,
                TileState.WRONG,
                TileState.WRONG
            ),
            listOf(
                TileState.WRONG,
                TileState.WRONG,
                TileState.WRONG,
                TileState.WRONG,
                TileState.WRONG
            )
        )
    }


}
