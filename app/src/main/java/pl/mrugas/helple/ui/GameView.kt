package pl.mrugas.helple.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.material.ButtonDefaults.textButtonColors
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.mrugas.helple.GameViewModel
import pl.mrugas.helple.GameViewModel.Companion.WORD_LEN
import pl.mrugas.helple.R

@Preview
@Composable
fun MainActivityView(gameViewModel: GameViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GameView(gameState = gameViewModel.gameState) { word, tile -> gameViewModel.updateState(word, tile) }
        Row {
            Button(onClick = { gameViewModel.updateHints() }) {
                Text(text = "OK")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(onClick = { gameViewModel.restart() }) {
                Text(text = "Restart")
            }
        }
    }

}

data class GameState(val words: List<WordState>, val wordLen : Int = 5, val attempt: Int = 0) {
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

        fun initial(initialWord: String) = GameState(
            listOf(
                WordState(
                    0,
                    List(WORD_LEN) { tileId ->
                        Tile(tileId, TileState.CORRECT_PLACE, initialWord[tileId])
                    }
                )
            )
        )
    }
}

@Preview
@Composable
fun GameView(gameState: State<GameState>, onGameStateChanged: (WordState, Tile) -> Unit) {
    for ((idx, word) in gameState.value.words.withIndex()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            WordView(
                word = word,
                locked = gameState.value.attempt != idx,
                onWordChanged = { word, tile -> onGameStateChanged(word, tile) })
        }
    }
}


data class WordState(val attempt: Int, val tiles: List<Tile>) {
    val word: String
        get() = tiles.map { it.letter }.joinToString(separator = "")
}

@Preview
@Composable
fun WordView(word: WordState, locked: Boolean, onWordChanged: (WordState, Tile) -> Unit) {
    for (tile in word.tiles) {
        TileView(tile = tile, locked = locked, onTileChanged = { onWordChanged(word, it) })
    }
}

enum class TileState {
    CORRECT_PLACE, INCORRECT_PLACE, WRONG;

    fun next(): TileState {
        return values().toMutableList().apply { add(values().first()) }.let {
            val idx = it.indexOf(this)
            it[idx + 1]
        }
    }
}

data class Tile(val id: Int, val state: TileState, val letter: Char)

@Preview
@Composable
fun TileView(@PreviewParameter(TileProvider::class) tile: Tile, locked: Boolean = false, onTileChanged: (Tile) -> Unit = {}) {
    val color = when (tile.state) {
        TileState.CORRECT_PLACE -> colorResource(id = R.color.tile_correct)
        TileState.INCORRECT_PLACE -> colorResource(id = R.color.tile_incorrect_place)
        TileState.WRONG -> colorResource(id = R.color.tile_wrong_letter)
    }
    val textColor = if (tile.state == TileState.WRONG) Color.White else Color.Black
    return OutlinedButton(
        modifier = Modifier
            .padding(16.dp)
            .size(width = 48.dp, height = 48.dp),
        border = BorderStroke(1.dp, color),
        colors = textButtonColors(backgroundColor = color, contentColor = Color.Black),
        elevation = elevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        onClick = { onTileChanged(tile) },
        enabled = !locked
    ) {
        Text(
            text = tile.letter.uppercase(),
            color = textColor
        )
    }
}


class TileProvider : PreviewParameterProvider<Tile> {
    override val values = listOf(Tile(0, TileState.INCORRECT_PLACE, 'A')).asSequence()
}
