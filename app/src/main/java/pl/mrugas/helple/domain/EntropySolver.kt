package pl.mrugas.helple.domain

import java.util.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.coroutineScope
import pl.mrugas.helple.data.DbWord
import pl.mrugas.helple.data.QueryBuilder
import pl.mrugas.helple.data.WordDao
import pl.mrugas.helple.ui.GameState
import pl.mrugas.helple.ui.TileState
import pl.mrugas.helple.ui.WordState
import pl.mrugas.helple.util.nAryCartesianProduct
import kotlin.math.log2

class EntropySolver : Solver {
    @OptIn(ObsoleteCoroutinesApi::class)
    override suspend fun guessNewWord(
        gameState: GameState,
        wordDao: WordDao,
        updateProgress: (progress: Float) -> Unit
    ): DbWord? = coroutineScope {
        val query = QueryBuilder.fromGameState(gameState)
        val allWordsQuery = QueryBuilder().setWordsLen(gameState.wordLen)
        val possibleWords = wordDao.rawQuery(query.build())
        val possibleHints = getPossibleHints(gameState)
        val allWordsCount = wordDao.rawCountQuery(allWordsQuery.copy().count().build())
        val counter = actor<Any> {
            var counter = 0
            for (msg in channel) {
                counter++
                updateProgress(counter.toFloat() / possibleWords.size)
            }
        }
        val rating = possibleWords
            .map { word ->
                async {
                    val entropy = countEntropy(
                        gameState = gameState,
                        wordDao = wordDao,
                        word = word.toString(),
                        possibleHints = possibleHints,
                        allWordsCount = allWordsCount
                    )
                    counter.send(Unit)
                    Pair(entropy, word)
                }
            }
            .awaitAll()
        counter.close()
        rating.maxByOrNull { it.first }?.second
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
        val random = Random(SEED)
        val hintsUsed = when (gameState.attempt) {
            0 -> 16
            1 -> 25
            else -> 70
        }
        return nAryCartesianProduct<TileState>(List(gameState.wordLen) { TileState.values().toList() })
            .filter { states -> states.any { state -> state != TileState.CORRECT_PLACE } }
            .shuffled(random)
            .take(hintsUsed)
            .toList()
    }
}

private const val SEED = 1410L
