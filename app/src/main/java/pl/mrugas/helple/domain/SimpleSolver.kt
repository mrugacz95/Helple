package pl.mrugas.helple.domain

import pl.mrugas.helple.data.DbWord
import pl.mrugas.helple.data.QueryBuilder
import pl.mrugas.helple.data.WordDao
import pl.mrugas.helple.ui.GameState

class SimpleSolver : Solver {
    override suspend fun guessNewWord(
        gameState: GameState,
        wordDao: WordDao,
        updateProgress: (progress: Float) -> Unit
    ): DbWord? = wordDao.rawQuery(QueryBuilder.fromGameState(gameState).build()).randomOrNull()
}
