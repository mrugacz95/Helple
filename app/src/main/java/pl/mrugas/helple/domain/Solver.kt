package pl.mrugas.helple.domain

import pl.mrugas.helple.data.DbWord
import pl.mrugas.helple.data.WordDao
import pl.mrugas.helple.ui.GameState

interface Solver {
    suspend fun guessNewWord(
        gameState: GameState,
        wordDao: WordDao,
        updateProgress: (progress: Float) -> Unit
    ): DbWord?
}
