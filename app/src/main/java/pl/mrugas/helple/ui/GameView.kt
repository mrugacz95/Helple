package pl.mrugas.helple.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.mrugas.helple.GameViewModel
import pl.mrugas.helple.GameViewModel.Companion.WORD_LEN

@Composable
fun MainActivityView(gameViewModel: GameViewModel = viewModel()) {
    GameView(gameState = gameViewModel.gameState.value,
        onGameStateChanged = { word, tile -> gameViewModel.updateState(word, tile) },
        guessNewWordAction = { gameViewModel.guessNewWord() },
        restartAction = { gameViewModel.restart() },
        changeWordLengthAction = { gameViewModel.changeWordLength(it) })
}

data class GameState(
    val words: List<WordState>,
    val wordLen: Int = 5,
    val attempt: Int = 0,
    val possibleWords: Int? = null,
    val failed: Boolean = false,
    val loading: LoadingState? = null,
    val won: Boolean = false,
    val solver: SolverType = SolverType.MinimaxSolverType,
) {
    val tiles = words.flatMap { it.tiles }

    companion object {
        fun randomState(): GameState {
            val word = (0..WORD_LEN).map { ('A'..'Z').random() }.joinToString(separator = "")
            return GameState(
                listOf(
                    WordState(
                        0,
                        List(WORD_LEN) { tileId ->
                            Tile(tileId, TileState.values().random(), word[tileId])
                        }
                    )
                )
            )
        }

        fun initial(initialWord: String, possibleWords: Int, loading: LoadingState? = null) = GameState(
            words = listOf(
                WordState(
                    attempt = 0,
                    tiles = List(initialWord.length) { tileId ->
                        Tile(tileId, TileState.CORRECT_PLACE, initialWord[tileId])
                    }
                )
            ),
            wordLen = initialWord.length,
            possibleWords = possibleWords,
            loading = loading,
        )

        fun empty() = GameState(emptyList(), loading = LoadingState.Circular)
    }
}

sealed class LoadingState {
    object Circular : LoadingState()
    data class Progress(val value: Float) : LoadingState()
}

enum class SolverType { SimpleSolverType, MinimaxSolverType, ExploreExploitSolverType }

@Preview
@Composable
fun GameView(
    @PreviewParameter(GameProvider::class) gameState: GameState,
    onGameStateChanged: (WordState, Tile) -> Unit = { _, _ -> },
    guessNewWordAction: () -> Unit = {},
    restartAction: () -> Unit = {},
    changeWordLengthAction: (Int) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (wordLen in 5..6) {
                    Button(
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .padding(0.dp)
                            .size(38.dp),
                        onClick = { changeWordLengthAction(wordLen) },
                        enabled = gameState.loading == null,
                        shape = CircleShape
                    ) {
                        Text(text = wordLen.toString(), modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        for ((idx, _) in gameState.words.withIndex()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                WordView(
                    wordId = idx,
                    gameState = gameState,
                    onWordChanged = onGameStateChanged,
                )
            }
        }
        if (gameState.loading != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                when (gameState.loading) {
                    is LoadingState.Circular -> {
                        CircularProgressIndicator()
                    }
                    is LoadingState.Progress -> {
                        LinearProgressIndicator(progress = gameState.loading.value, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        ControlsView(
            gameState = gameState,
            guessNewWordAction = guessNewWordAction,
            restartAction = restartAction
        )
    }
}

class GameProvider : PreviewParameterProvider<GameState> {
    override val values = listOf(
        GameState.initial("siorka", possibleWords = 35263, loading = LoadingState.Progress(0.33f)),
        GameState.initial("korei", possibleWords = 32263, loading = null),
    ).asSequence()
}

