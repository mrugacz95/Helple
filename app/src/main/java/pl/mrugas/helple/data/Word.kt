package pl.mrugas.helple.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Word(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "letter0") val letter0: String,
    @ColumnInfo(name = "letter1") val letter1: String,
    @ColumnInfo(name = "letter2") val letter2: String,
    @ColumnInfo(name = "letter3") val letter3: String,
    @ColumnInfo(name = "letter4") val letter4: String,
)
