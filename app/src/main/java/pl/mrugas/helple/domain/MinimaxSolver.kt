package pl.mrugas.helple.domain

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
import pl.mrugas.helple.util.CombsWithReps

class MinimaxSolver : Solver {

    @OptIn(ObsoleteCoroutinesApi::class)
    override suspend fun guessNewWord(
        gameState: GameState,
        wordDao: WordDao,
        updateProgress: (progress: Float) -> Unit
    ): DbWord? = coroutineScope {
        val query = QueryBuilder.fromGameState(gameState)
        val possibleWords = wordDao.rawQuery(query.build())
        val possibleHints = CombsWithReps(gameState.wordLen, TileState.values().size, TileState.values().toList())
            .generate()
            .filter { states -> states.any { state -> state != TileState.CORRECT_PLACE } }
            .toList()
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
                    val worstResult = countWorstResult(gameState, wordDao, word.toString(), possibleHints)
                    counter.send(Unit)
                    worstResult to word
                }
            }
            .awaitAll()
        counter.close()
        return@coroutineScope rating.minByOrNull { it.first }?.second
    }

    private suspend fun countWorstResult(
        gameState: GameState,
        wordDao: WordDao,
        word: String,
        possibleHints: List<List<TileState>>
    ): Int = coroutineScope {
        val query = QueryBuilder.fromGameState(gameState).count()
        possibleHints.map { hint ->
            async {
                val queryCopy = query.copy()
                val wordState = WordState.fromWordAndStates(word, hint)
                for (tile in wordState.tiles) {
                    queryCopy.appendTileRuleToQuery(tile.state, wordState, tile.letter, tile.id, word.length, queryCopy)
                }
                wordDao.rawCountQuery(queryCopy.build())
            }
        }
            .awaitAll()
            .maxBy { it }
    }
}
