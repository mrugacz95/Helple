package pl.mrugas.helple.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp


@Preview
@Composable
fun ControlsView(
    @PreviewParameter(GameProvider::class) gameState: GameState,
    guessNewWordAction: () -> Unit = {},
    restartAction: () -> Unit = {}
) {
    Row(
        Modifier.height(IntrinsicSize.Min)
    ) {
        Column(
            Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Possible words: ${gameState.possibleWords}"
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            onClick = { guessNewWordAction() },
            enabled = gameState.loading == null
        ) {
            Text(text = "OK")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            onClick = { restartAction() },
            enabled = gameState.loading == null
        ) {
            val text = if (gameState.failed) "No words found, restart" else "Restart"
            Text(text = text)
        }
    }
}
