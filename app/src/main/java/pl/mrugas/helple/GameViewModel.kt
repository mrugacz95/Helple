package pl.mrugas.helple

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import pl.mrugas.helple.data.DbWord
import pl.mrugas.helple.data.QueryBuilder
import pl.mrugas.helple.data.WordDao
import pl.mrugas.helple.domain.ExploreExploitSolver
import pl.mrugas.helple.domain.MinimaxSolver
import pl.mrugas.helple.domain.SimpleSolver
import pl.mrugas.helple.ui.GameState
import pl.mrugas.helple.ui.LoadingState
import pl.mrugas.helple.ui.SolverType
import pl.mrugas.helple.ui.Tile
import pl.mrugas.helple.ui.TileState
import pl.mrugas.helple.ui.WordState
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@HiltViewModel
class GameViewModel @Inject constructor(private val wordDao: WordDao) : ViewModel() {

    var gameState = mutableStateOf(GameState.empty())
        private set

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            val count = wordDao.count(gameState.value.wordLen)
            gameState.value = GameState.initial(INITIAL_WORDS[gameState.value.wordLen] ?: "korei", possibleWords = count)
        }
    }

    fun guessNewWord() {
        viewModelScope.launch(Dispatchers.Default) {
            val wordsLeft = wordDao.rawCountQuery(QueryBuilder.fromGameState(gameState.value).count().build())

            if (gameState.value.words.last().tiles.all { it.state == TileState.CORRECT_PLACE }) {
                gameState.value = gameState.value.copy(won = true, possibleWords = 1)
                return@launch
            }

            val currentState = gameState.value.copy(
                loading = LoadingState.Progress(0f),
                possibleWords = wordsLeft
            )
            gameState.value = currentState

            val newWord = calculateNewWord(currentState) { progress ->
                gameState.value = currentState.copy(
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
                possibleWords = wordsLeft,
                failed = false,
                loading = null
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun calculateNewWord(gameState: GameState, updateProgress: (progress: Float) -> Unit): DbWord? {
        val solver = when (gameState.solver) {
            SolverType.SimpleSolverType -> SimpleSolver()
            SolverType.MinimaxSolverType -> MinimaxSolver()
            SolverType.ExploreExploitSolverType -> ExploreExploitSolver()
        }
        val newWord: DbWord?
        val elapsedTime = measureTimeMillis { newWord = solver.guessNewWord(gameState, wordDao, updateProgress) }
        Log.d("Solver", "Calculating new Word took ${elapsedTime.toDuration(DurationUnit.MILLISECONDS).toIsoString()}")
        return newWord
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
        viewModelScope.coroutineContext.cancelChildren()
        init()
    }

    fun changeWordLength(newLength: Int) {
        gameState.value = gameState.value.copy(wordLen = newLength)
        restart()
    }

    companion object {
        val INITIAL_WORDS = mapOf(
            5 to "korei", // korei, siora, eolia
            6 to "siorka"
        )
        val WORD_LEN = 5
    }
}
