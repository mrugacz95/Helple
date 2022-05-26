package pl.mrugas.helple.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery

@Dao
interface WordDao {
    @Query("SELECT count(*) from words")
    suspend fun count() : Int

    @RawQuery
    suspend fun rawQuery(query: SimpleSQLiteQuery): List<DbWord>
}
