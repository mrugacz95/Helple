package pl.mrugas.helple.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.material.ButtonDefaults.textButtonColors
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import pl.mrugas.helple.GameViewModel
import pl.mrugas.helple.MainActivity
import pl.mrugas.helple.R

@Preview
@Composable
fun MainActivityView(gameViewModel: GameViewModel = viewModel()){
            GameView(gameState = gameViewModel.gameState)
}

data class GameState(val words: List<WordState>) {
    companion object {
        fun randomState(): GameState {
            return GameState(
                List(6) {
                    WordState(
                        List(5) {
                            Tile(TileState.values().random(), ('A'..'Z').random())
                        }
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun GameView(gameState: State<GameState> ) {
    return Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        for (word in gameState.value.words) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                WordView(word = word)
            }
        }
    }
}


data class WordState(val tiles: List<Tile>)

@Preview
@Composable
fun WordView(word: WordState) {
    for (tile in word.tiles) {
        TileView(tile = tile)
    }
}

enum class TileState {
    UNKNOWN, CORRECT_PLACE, INCORRECT_PLACE, WRONG;

    fun next(): TileState {
        return values().toMutableList().apply { add(values().first()) }.let {
            val idx = it.indexOf(this)
            it[idx + 1]
        }
    }
}

data class Tile(val state: TileState, val letter: Char?)

@Preview
@Composable
fun TileView(@PreviewParameter(TileProvider::class) tile: Tile) {
    var state by remember { mutableStateOf(tile.state) }
    val color = when (state) {
        TileState.UNKNOWN -> colorResource(id = R.color.tile_unknown)
        TileState.CORRECT_PLACE -> colorResource(id = R.color.tile_correct)
        TileState.INCORRECT_PLACE -> colorResource(id = R.color.tile_incorrect_place)
        TileState.WRONG -> colorResource(id = R.color.tile_wrong_letter)
    }
    val textColor = if (state == TileState.WRONG) Color.White else Color.Black
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
        onClick = {
            state = state.next()
        },
    ) {
        Text(
            text = tile.letter.toString(),
            color = textColor
        )
    }
}


class TileProvider : PreviewParameterProvider<Tile> {
    override val values = listOf(Tile(TileState.INCORRECT_PLACE, 'A')).asSequence()
}
