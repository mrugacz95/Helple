package pl.mrugas.helple.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

data class WordState(val attempt: Int, val tiles: List<Tile>) {
    val word: String
        get() = tiles.map { it.letter }.joinToString(separator = "")
}

@Preview
@Composable
fun WordView(
    @PreviewParameter(WordProvider::class) wordState: WordState,
    locked: Boolean = false,
    onWordChanged: (WordState, Tile) -> Unit = { _, _ -> }
) {
    Row {
        for (tile in wordState.tiles) {
            TileView(
                tile = tile,
                wordLen = wordState.word.length,
                locked = locked,
                onTileChanged = { onWordChanged(wordState, it) })
        }
    }
}

class WordProvider : PreviewParameterProvider<WordState> {
    override val values: Sequence<WordState> = listOf(
        WordState(
            1,
            tiles = "kotek".mapIndexed { idx, letter -> Tile(id = idx, letter = letter, state = TileState.INCORRECT_PLACE) })
    ).asSequence()
}
