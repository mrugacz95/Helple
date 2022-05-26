package pl.mrugas.helple

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import pl.mrugas.helple.data.DbWord
import pl.mrugas.helple.data.WordDao
import pl.mrugas.helple.ui.GameState
import pl.mrugas.helple.ui.Tile
import pl.mrugas.helple.ui.TileState
import pl.mrugas.helple.ui.WordState

@HiltViewModel
class GameViewModel @Inject constructor(private val wordDao: WordDao) : ViewModel() {

    var gameState = mutableStateOf(GameState.initial(INITIAL_WORD))
        private set

    fun init() {
        viewModelScope.launch {
            // TODO fetch initial word from db
        }
    }

    fun updateHints() {
        val currentState = gameState.value

        viewModelScope.launch {
            val newWord = newWordGuess(currentState)

            val attemptCount = currentState.attempt + 1
            gameState.value = GameState(
                words = currentState.words.toMutableList() + listOf(newWord.toWordState(attemptCount)),
                attempt = attemptCount
            )
        }
    }

    private suspend fun newWordGuess(gameState: GameState): DbWord {

        val query = StringBuilder("SELECT * FROM words WHERE TRUE ")
        for (word in gameState.words) {
            for (tile in word.tiles) {
                when (tile.state) {
                    TileState.CORRECT_PLACE -> query.append("AND letter${tile.id} is \"${tile.letter}\" ")
                    TileState.INCORRECT_PLACE -> {
                        query.append("AND letter${tile.id} is not \"${tile.letter}\" AND (FALSE ")
                        for (i in 0..4) {
                            if (i != tile.id) {
                                query.append("OR letter$i is \"${tile.letter}\" ")
                            }
                        }
                        query.append(") ")
                    }
                    TileState.WRONG -> {
                        query.append("AND letter${tile.id} is not \"${tile.letter}\" ")
                    }
                }
            }
        }
        query.append("AND length is ${gameState.wordLen} LIMIT 1")
        Log.d("query", query.toString())
        return wordDao.rawQuery(
            SimpleSQLiteQuery(query.toString())
        ).first()

    }

    private fun gerneratePossibleHints(){

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
        gameState.value = GameState.initial(INITIAL_WORD)
    }

    companion object {
        val INITIAL_WORD = "korei"
        val WORD_LEN = 5
    }
}
