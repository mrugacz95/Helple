package pl.mrugas.helple.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter

data class WordState(val attempt: Int, val tiles: List<Tile>) {
    companion object {
        fun fromWordAndStates(word: String, hints: List<TileState>, attempt: Int = -1): WordState {
            return WordState(attempt = attempt,
                tiles = word.toList()
                    .zip(hints)
                    .mapIndexed { idx, tile -> Tile(idx, tile.second, tile.first) })
        }
    }
}

@Preview
@Composable
fun WordView(
    @PreviewParameter(GameProvider::class) gameState: GameState,
    wordId: Int = 0,
    onWordChanged: (WordState, Tile) -> Unit = { _, _ -> }
) {
    val wordState = gameState.words[wordId]
    Row {
        for (tile in wordState.tiles.withIndex()) {
            TileView(
                gameState = gameState,
                tileId = tile.index,
                wordId = wordId,
                onTileChanged = { onWordChanged(wordState, it) })
        }
    }
}
