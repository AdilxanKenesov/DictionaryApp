package uz.gita.dictionaryapp.data.repository_impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import uz.gita.dictionaryapp.data.source.local.user.dao.QuizResultDao
import uz.gita.dictionaryapp.data.source.local.user.entity.QuizResultEntity
import uz.gita.dictionaryapp.domain.model.QuizMode
import uz.gita.dictionaryapp.domain.model.QuizStats
import uz.gita.dictionaryapp.domain.repository.QuizRepository
import uz.gita.dictionaryapp.utils.resultOf
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val dao: QuizResultDao
) : QuizRepository {

    override suspend fun saveResult(score: Int, total: Int, mode: QuizMode): Result<Unit> = resultOf {
        dao.insert(QuizResultEntity(score = score, total = total, mode = mode.name, createdAt = System.currentTimeMillis()))
    }

    override fun getStats(): Flow<Result<QuizStats>> = dao.getAll()
        .map { results ->
            val best = results.maxByOrNull { it.score.toFloat() / it.total }
            Result.success(
                QuizStats(
                    count = results.size,
                    bestScore = best?.score ?: 0,
                    bestTotal = best?.total ?: 0,
                    averagePercent = if (results.isEmpty()) 0
                    else results.map { it.score * 100 / it.total }.average().toInt()
                )
            )
        }
        .catch { emit(Result.failure(it)) }
}
