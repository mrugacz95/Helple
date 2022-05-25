package pl.mrugas.helple.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface WordDao {
    @Query("SELECT * FROM word  ORDER BY letter3 ASC limit 6")
    suspend fun getSomeWords(): List<DbWord>

    @Query("SELECT count(*) from word")
    suspend fun count() : Int
}
