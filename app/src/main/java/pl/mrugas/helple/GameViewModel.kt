package pl.mrugas.helple

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import pl.mrugas.helple.data.WordDao
import pl.mrugas.helple.ui.GameState

@HiltViewModel
class GameViewModel @Inject constructor(private val wordDao: WordDao) : ViewModel() {

    var gameState = mutableStateOf(GameState.randomState())
        private set

    fun init() {
        viewModelScope.launch {
            val newGameState = GameState(wordDao.getSomeWords().map { it.toWordState() })
            gameState.value = newGameState
        }
    }
}
