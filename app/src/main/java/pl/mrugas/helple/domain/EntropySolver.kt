package pl.mrugas.helple.domain

import java.util.*
import pl.mrugas.helple.data.DbWord
import pl.mrugas.helple.data.QueryBuilder
import pl.mrugas.helple.data.WordDao
import pl.mrugas.helple.ui.GameState
import pl.mrugas.helple.ui.TileState
import pl.mrugas.helple.ui.WordState
import pl.mrugas.helple.util.nAryCartesianProduct
import kotlin.math.log2

class EntropySolver : Solver {
    override suspend fun guessNewWord(
        gameState: GameState,
        wordDao: WordDao,
        updateProgress: (progress: Float) -> Unit
    ): DbWord? {
        val query = QueryBuilder.fromGameState(gameState)
        val allWordsQuery = QueryBuilder().setWordsLen(gameState.wordLen)
        val possibleWords = wordDao.rawQuery(query.build())
        val possibleHints = getPossibleHints(gameState)
        val allWordsCount = wordDao.rawCountQuery(allWordsQuery.copy().count().build())
        return possibleWords
            .withIndex()
            .maxByOrNull { (idx, word) ->
                val entropy = countEntropy(
                    gameState = gameState,
                    wordDao = wordDao,
                    word = word.toString(),
                    possibleHints = possibleHints,
                    allWordsCount = allWordsCount
                )
                updateProgress(idx.toFloat() / possibleWords.size)
                entropy
            }?.value
    }

    private suspend fun countEntropy(
        gameState: GameState,
        wordDao: WordDao,
        word: String,
        possibleHints: List<List<TileState>>,
        allWordsCount: Int,
    ): Float {
        var entropy = 0f
        val query = QueryBuilder.fromGameState(gameState).count()
        for (hint in possibleHints) {
            val queryCopy = query.copy()

            val wordState = WordState.fromWordAndStates(word, hint)
            for (tile in wordState.tiles) {
                queryCopy.appendTileRuleToQuery(tile.state, wordState, tile.letter, tile.id, word.length, queryCopy)
            }
            val p = wordDao.rawCountQuery(queryCopy.build()).toFloat() / allWordsCount
            if (p > 0) {
                entropy -= p * log2(p)
            }
        }
        return entropy
    }

    private fun getPossibleHints(gameState: GameState): List<List<TileState>> {
        val random = Random(1410)
        val hintsUsed = when (gameState.attempt) {
            0 -> 8
            1 -> 10
            else -> 15
        }
        return nAryCartesianProduct<TileState>(List(gameState.wordLen) { TileState.values().toList() })
            .filter { states -> states.any { state -> state != TileState.CORRECT_PLACE } }
            .shuffled(random)
            .take(hintsUsed)
            .toList()
    }
}
