package pl.mrugas.helple.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface WordDao {
    @Query("SELECT * FROM word limit 5")
    fun getSomeWords(): List<Word>

    @Query("SELECT count(*) from word")
    fun count() : Int
}
