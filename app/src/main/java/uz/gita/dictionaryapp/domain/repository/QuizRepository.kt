package uz.gita.dictionaryapp.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.gita.dictionaryapp.domain.model.QuizMode
import uz.gita.dictionaryapp.domain.model.QuizStats

interface QuizRepository {

    suspend fun saveResult(score: Int, total: Int, mode: QuizMode): Result<Unit>

    fun getStats(): Flow<Result<QuizStats>>
}
