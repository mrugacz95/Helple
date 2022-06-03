package pl.mrugas.helple.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import pl.mrugas.helple.ui.Tile
import pl.mrugas.helple.ui.TileState
import pl.mrugas.helple.ui.WordState

@Entity(
    tableName = "words",
    indices = [Index(
        name = "ix_words_length_letter",
        value = ["length", "letter0", "letter1", "letter2", "letter3", "letter4", "letter5"]
    )]
)
data class DbWord(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "letter0") val letter0: String,
    @ColumnInfo(name = "letter1") val letter1: String,
    @ColumnInfo(name = "letter2") val letter2: String,
    @ColumnInfo(name = "letter3") val letter3: String,
    @ColumnInfo(name = "letter4") val letter4: String,
    @ColumnInfo(name = "letter5") val letter5: String,
    @ColumnInfo(name = "length") val length: Int,
) {
    fun toWordState(attempt: Int): WordState {
        val word = toString()
        return WordState(
            attempt = attempt,
            tiles = List(word.length) {
                Tile(it, TileState.CORRECT_PLACE, word[it])
            }
        )
    }

    override fun toString(): String {
        return "$letter0$letter1$letter2$letter3$letter4$letter5"
    }
}
