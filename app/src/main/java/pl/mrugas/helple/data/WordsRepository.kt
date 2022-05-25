package pl.mrugas.helple.data

import javax.inject.Inject

class WordsRepository @Inject constructor(
    private val wordDao: WordDao
){
    suspend fun getCount() = wordDao.count()
}
