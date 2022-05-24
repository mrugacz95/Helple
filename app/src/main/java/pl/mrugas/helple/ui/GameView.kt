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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import pl.mrugas.helple.R


@Preview
@Composable
fun GameView() {
    return Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        for (i in 1..6) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                for (j in 1..5) {
                    TileView(Tile(TileState.values().random(), ('A'..'Z').random()))
                }
            }
        }
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
