package uz.gita.dictionaryapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.gita.dictionaryapp.domain.model.FavoriteSort
import uz.gita.dictionaryapp.domain.model.FavoriteWord

interface GetFavoritesUseCase {
    operator fun invoke(sort: FavoriteSort, query: String): Flow<Result<List<FavoriteWord>>>
}
