package uz.gita.dictionaryapp.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.gita.dictionaryapp.domain.model.FavoriteWord

interface FavoriteRepository {

    fun getFavorites(): Flow<Result<List<FavoriteWord>>>

    fun isFavorite(english: String): Flow<Boolean>

    fun getCount(): Flow<Int>

    suspend fun toggle(english: String): Result<Boolean>

    suspend fun remove(english: String): Result<Long?>

    suspend fun restore(english: String, addedAt: Long): Result<Unit>
}
