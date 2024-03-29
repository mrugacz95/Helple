package pl.mrugas.helple.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import pl.mrugas.helple.R


enum class TileState {
    CORRECT_PLACE, INCORRECT_PLACE, WRONG;
}

data class Tile(val id: Int, val state: TileState, val letter: Char)

@Preview
@Composable
fun TileView(
    @PreviewParameter(GameProvider::class) gameState: GameState,
    wordId: Int = 0,
    tileId: Int = 0,
    onTileChanged: (Tile) -> Unit = {}
) {
    val tile = gameState.words[wordId].tiles[tileId]
    val color = when (tile.state) {
        TileState.CORRECT_PLACE -> colorResource(id = R.color.tile_correct)
        TileState.INCORRECT_PLACE -> colorResource(id = R.color.tile_incorrect_place)
        TileState.WRONG -> colorResource(id = R.color.tile_wrong_letter)
    }
    val textColor = if (tile.state == TileState.WRONG) Color.White else Color.Black
    val size = 42.dp
    val infiniteTransition = rememberInfiniteTransition()
    val animationStartDelay = 75 * tile.id
    val animationDuration = 1000 + animationStartDelay
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = animationDuration
                0f at 0
                0f at animationStartDelay
                -6f at 200 + animationStartDelay with LinearEasing
                0f at animationDuration with FastOutSlowInEasing
            },
            repeatMode = RepeatMode.Restart
        )
    )
    val yOffset = if (gameState.won && gameState.attempt == wordId) {
        offset.dp
    } else {
        0.dp
    }
    return OutlinedButton(
        modifier = Modifier
            .padding(8.dp)
            .size(width = size, height = size)
            .offset(
                0.dp,
                yOffset
            ),
        border = BorderStroke(1.dp, color),
        colors = ButtonDefaults.textButtonColors(backgroundColor = color, contentColor = Color.Black),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            disabledElevation = 0.dp
        ),
        onClick = { onTileChanged(tile) },
        enabled = !gameState.won && gameState.loading == null && !gameState.failed && gameState.attempt == wordId
    ) {
        Text(
            text = tile.letter.uppercase(),
            color = textColor
        )
    }
}
