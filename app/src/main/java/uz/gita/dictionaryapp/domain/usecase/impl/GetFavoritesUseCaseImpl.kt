package uz.gita.dictionaryapp.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import uz.gita.dictionaryapp.domain.model.FavoriteSort
import uz.gita.dictionaryapp.domain.model.FavoriteWord
import kotlinx.coroutines.flow.map
import uz.gita.dictionaryapp.domain.repository.FavoriteRepository
import uz.gita.dictionaryapp.domain.usecase.GetFavoritesUseCase
import javax.inject.Inject

class GetFavoritesUseCaseImpl @Inject constructor(
    private val repository: FavoriteRepository
) : GetFavoritesUseCase {
    override fun invoke(sort: FavoriteSort, query: String): Flow<Result<List<FavoriteWord>>> =
        repository.getFavorites().map { result ->
            result.map { favorites ->
                val text = query.trim()
                val filtered = if (text.isEmpty()) favorites else favorites.filter {
                    it.word.english.contains(text, ignoreCase = true) ||
                            it.word.uzbek.contains(text, ignoreCase = true)
                }
                when (sort) {
                    FavoriteSort.NEWEST -> filtered.sortedByDescending { it.addedAt }
                    FavoriteSort.ALPHABET -> filtered.sortedBy { it.word.english.lowercase() }
                }
            }
        }
}
