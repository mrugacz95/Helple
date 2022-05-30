package pl.mrugas.helple.ui

import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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

data class Tile(val id: Int, val state: TileState, val letter: Char, val won: Boolean = false)

@Preview
@Composable
fun TileView(
    @PreviewParameter(TileProvider::class) tile: Tile,
    won: Boolean = false,
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
    val offset = if (won) {
        val lifted by remember { mutableStateOf(won) }
        val transition = updateTransition(targetState = lifted, label = "transition")
        val transitionOffset by transition.animateOffset(transitionSpec = {
            if (this.targetState) {
                tween(1200) // launch duration
            } else {
                tween(1500) // land duration
            }
        }, label = "offset") { animated ->
            if (animated) Offset(0f, 0f) else Offset(0f, -12f)
        }
        transitionOffset
    }
    else {
         remember { Offset(0f, 0f) }
    }
    return OutlinedButton(
        modifier = Modifier
            .padding(8.dp)
            .size(width = size, height = size)
            .offset(offset.x.dp, offset.y.dp),
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
