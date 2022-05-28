package pl.mrugas.helple.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import pl.mrugas.helple.R


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
fun TileView(
    @PreviewParameter(TileProvider::class) tile: Tile,
    wordLen: Int = 6,
    locked: Boolean = false,
    onTileChanged: (Tile) -> Unit = {}
) {
    val color = when (tile.state) {
        TileState.CORRECT_PLACE -> colorResource(id = R.color.tile_correct)
        TileState.INCORRECT_PLACE -> colorResource(id = R.color.tile_incorrect_place)
        TileState.WRONG -> colorResource(id = R.color.tile_wrong_letter)
    }
    val textColor = if (tile.state == TileState.WRONG) Color.White else Color.Black
    val size = 42.dp
    return OutlinedButton(
        modifier = Modifier
            .padding(8.dp)
            .size(width = size, height = size),
        border = BorderStroke(1.dp, color),
        colors = ButtonDefaults.textButtonColors(backgroundColor = color, contentColor = Color.Black),
        elevation = ButtonDefaults.elevation(
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
