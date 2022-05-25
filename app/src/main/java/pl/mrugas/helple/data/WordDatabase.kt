package pl.mrugas.helple.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DbWord::class], version = 1)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}
