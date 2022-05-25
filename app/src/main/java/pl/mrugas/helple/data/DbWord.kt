package pl.mrugas.helple.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import pl.mrugas.helple.ui.Tile
import pl.mrugas.helple.ui.TileState
import pl.mrugas.helple.ui.WordState

@Entity(tableName = "Word")
data class DbWord(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "letter0") val letter0: String,
    @ColumnInfo(name = "letter1") val letter1: String,
    @ColumnInfo(name = "letter2") val letter2: String,
    @ColumnInfo(name = "letter3") val letter3: String,
    @ColumnInfo(name = "letter4") val letter4: String,
) {
    fun toWordState(): WordState {
        return WordState(
            listOf(
                Tile(TileState.UNKNOWN, letter0.first().uppercaseChar()),
                Tile(TileState.UNKNOWN, letter1.first().uppercaseChar()),
                Tile(TileState.UNKNOWN, letter2.first().uppercaseChar()),
                Tile(TileState.UNKNOWN, letter3.first().uppercaseChar()),
                Tile(TileState.UNKNOWN, letter4.first().uppercaseChar()),
            )
        )
    }
}
