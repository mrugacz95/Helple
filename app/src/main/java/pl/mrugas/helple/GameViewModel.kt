package pl.mrugas.helple

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.mrugas.helple.data.DbWord
import pl.mrugas.helple.data.WordDao
import pl.mrugas.helple.domain.QueryBuilder
import pl.mrugas.helple.ui.GameState
import pl.mrugas.helple.ui.LoadingState
import pl.mrugas.helple.ui.Tile
import pl.mrugas.helple.ui.TileState
import pl.mrugas.helple.ui.WordState
import kotlin.math.max
import kotlin.math.roundToInt

@HiltViewModel
class GameViewModel @Inject constructor(private val wordDao: WordDao) : ViewModel() {

    var gameState = mutableStateOf(GameState.empty())
        private set

    fun init() {
        restart()
    }

    fun guessNewWord() {
        val currentState = gameState.value
        gameState.value = currentState.copy(loading = LoadingState.Circular)

        viewModelScope.launch(Dispatchers.Default) {
            val newWord = calculateNewWord(currentState) { progress ->
                gameState.value = currentState.copy(
                    failed = true,
                    loading = LoadingState.Progress(progress)
                )
            }

            if (newWord == null) {
                gameState.value = currentState.copy(failed = true, loading = null)
                return@launch
            }

            val attemptCount = currentState.attempt + 1
            gameState.value = GameState(
                words = currentState.words.toMutableList() + listOf(newWord.toWordState(attemptCount)),
                attempt = attemptCount,
                wordLen = currentState.wordLen,
                possibleWords = wordDao.rawCountQuery(currentState.toQuery().build()),
                failed = false,
                loading = null
            )
        }
    }

    private suspend fun calculateNewWord(currentState: GameState, updateProgress: (Float) -> Unit): DbWord? {
        val query = currentState.toQuery()
        val possibleWords = wordDao.rawQuery(query.build())
        val possibleHints = generatePossibleHints(currentState.wordLen)
        var bestWord: DbWord? = null
        var minPossibleWordsLength = Int.MAX_VALUE
        var progress = 0f
        for ((idx, word) in possibleWords.withIndex()) {

            val worstResult = countWorstResult(query, word.toString(), possibleHints)

            if (worstResult < minPossibleWordsLength) {
                bestWord = word
                minPossibleWordsLength = worstResult
            }

            (idx / possibleWords.size * 100f).roundToInt().toFloat().let { newProgress ->
                if (newProgress > progress) {
                    progress = newProgress
                    updateProgress(progress)
                }
            }
        }
        return bestWord
    }

    private suspend fun GameState.toQuery(): QueryBuilder {
        val query = QueryBuilder()
        for (word in words) {
            for (tile in word.tiles) {
                appendTileRuleToQuery(tile.state, tile.letter, tile.id, wordLen, query)
            }
        }
        query.setWordsLen(wordLen)
        val result = wordDao.rawQuery(query.build())
        Log.d("query", "$query, count: ${result.size}")
        return query

    }

    private fun appendTileRuleToQuery(state: TileState, letter: Char, position: Int, wordLen: Int, query: QueryBuilder) {
        when (state) {
            TileState.CORRECT_PLACE -> query.addKnownLetter(letter, position)
            TileState.INCORRECT_PLACE -> query.addIncorrectPlaceLetter(letter, position, wordLen)
            TileState.WRONG -> query.addWrongLetter(letter, position)
        }
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

    private suspend fun countWorstResult(queryBuilder: QueryBuilder, word: String, possibleHints: List<List<TileState>>): Int {
        var worst = 0
        for (hint in possibleHints) {
            val query = queryBuilder.copy().count()
            for ((idx, letterWithState) in word.toList().zip(hint).withIndex()) {
                val (letter, state) = letterWithState
                appendTileRuleToQuery(state, letter, idx, word.length, query)
            }
            val result = wordDao.rawCountQuery(query.build())
            worst = max(worst, result)
        }
        return worst
    }

    fun updateState(word: WordState, tile: Tile) {
        val currentState = gameState.value
        val currentWords = currentState.words
        val newWords = currentWords.toMutableList().apply {
            this[word.attempt] = updateWord(this[word.attempt], tile.copy(state = tile.state.next()))
        }
        gameState.value = currentState.copy(words = newWords)
    }

    fun updateWord(word: WordState, newTile: Tile): WordState {
        return word.copy(tiles = word
            .tiles
            .toMutableList()
            .apply {
                this[newTile.id] = newTile
            }
        )
    }

    fun restart() {
        viewModelScope.launch(Dispatchers.IO) {
            val count = wordDao.count(gameState.value.wordLen)
            gameState.value = GameState.initial(INITIAL_WORDS[gameState.value.wordLen] ?: "korei", possibleWords = count)
        }
    }

    fun changeWordLength(newLength: Int) {
        gameState.value = gameState.value.copy(wordLen = newLength)
        restart()
    }

    companion object {
        val INITIAL_WORDS = mapOf(
            5 to "korea", // korei, siorka, eolia
            6 to "siorka"
        )
        val WORD_LEN = 5
    }
}
