package uz.gita.dictionaryapp.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.gita.dictionaryapp.domain.model.WordItem

interface HistoryRepository {

    fun getRecent(): Flow<Result<List<WordItem>>>

    suspend fun add(english: String): Result<Unit>

    suspend fun remove(english: String): Result<Unit>

    suspend fun clear(): Result<Unit>
}
