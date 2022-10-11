package pl.mrugas.helple.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import pl.mrugas.helple.R


@Preview
@Composable
fun ControlsView(
    @PreviewParameter(GameProvider::class) gameState: GameState,
    guessNewWordAction: () -> Unit = {},
    restartAction: () -> Unit = {},
    undoWord: () -> Unit = {}
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            Column(
                Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Possible words: ${gameState.possibleWords}"
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { restartAction() },
            ) {
                Text(text = stringResource(R.string.restart))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { undoWord() },
                enabled = gameState.attempt > 0
            ) {
                Text(text = stringResource(R.string.undo))
            }
        }
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { guessNewWordAction() },
                enabled = gameState.loading == null && !gameState.won && !gameState.failed,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.tile_correct)
                )
            ) {
                Text(text = stringResource(R.string.ok))
            }
        }
    }
}
