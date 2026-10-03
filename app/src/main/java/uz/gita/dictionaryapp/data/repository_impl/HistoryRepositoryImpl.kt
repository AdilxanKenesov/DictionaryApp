package uz.gita.dictionaryapp.data.repository_impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import uz.gita.dictionaryapp.data.source.local.user.dao.HistoryDao
import uz.gita.dictionaryapp.data.source.local.user.entity.HistoryEntity
import uz.gita.dictionaryapp.domain.model.WordItem
import uz.gita.dictionaryapp.domain.repository.DictionaryRepository
import uz.gita.dictionaryapp.domain.repository.HistoryRepository
import uz.gita.dictionaryapp.utils.resultOf
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val dao: HistoryDao,
    private val dictionaryRepository: DictionaryRepository
) : HistoryRepository {

    override fun getRecent(): Flow<Result<List<WordItem>>> = dao.getRecent(RECENT_LIMIT)
        .map { history -> dictionaryRepository.getWordItems(history.map { it.english }) }
        .catch { emit(Result.failure(it)) }

    override suspend fun add(english: String): Result<Unit> = resultOf {
        dao.insert(HistoryEntity(english, System.currentTimeMillis()))
    }

    override suspend fun remove(english: String): Result<Unit> = resultOf { dao.delete(english) }

    override suspend fun clear(): Result<Unit> = resultOf { dao.deleteAll() }

    private companion object {
        const val RECENT_LIMIT = 20
    }
}
