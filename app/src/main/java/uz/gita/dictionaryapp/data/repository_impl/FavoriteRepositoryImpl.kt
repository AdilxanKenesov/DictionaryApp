package uz.gita.dictionaryapp.data.repository_impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import uz.gita.dictionaryapp.data.source.local.user.dao.FavoriteDao
import uz.gita.dictionaryapp.data.source.local.user.entity.FavoriteEntity
import uz.gita.dictionaryapp.domain.model.FavoriteWord
import uz.gita.dictionaryapp.domain.repository.DictionaryRepository
import uz.gita.dictionaryapp.domain.repository.FavoriteRepository
import uz.gita.dictionaryapp.utils.resultOf
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val dao: FavoriteDao,
    private val dictionaryRepository: DictionaryRepository
) : FavoriteRepository {

    override fun getFavorites(): Flow<Result<List<FavoriteWord>>> = dao.getAll()
        .map { favorites ->
            dictionaryRepository.getWordItems(favorites.map { it.english }).map { words ->
                val addedAt = favorites.associate { it.english.lowercase() to it.addedAt }
                words.map { FavoriteWord(it, addedAt[it.english.lowercase()] ?: 0L) }
            }
        }
        .catch { emit(Result.failure(it)) }

    override fun isFavorite(english: String): Flow<Boolean> = dao.isFavorite(english)

    override fun getCount(): Flow<Int> = dao.getCount()

    override suspend fun toggle(english: String): Result<Boolean> = resultOf {
        if (dao.getByEnglish(english) != null) {
            dao.delete(english)
            false
        } else {
            dao.insert(FavoriteEntity(english, System.currentTimeMillis()))
            true
        }
    }

    override suspend fun remove(english: String): Result<Long?> = resultOf {
        val favorite = dao.getByEnglish(english)
        dao.delete(english)
        favorite?.addedAt
    }

    override suspend fun restore(english: String, addedAt: Long): Result<Unit> = resultOf {
        dao.insert(FavoriteEntity(english, addedAt))
    }
}
