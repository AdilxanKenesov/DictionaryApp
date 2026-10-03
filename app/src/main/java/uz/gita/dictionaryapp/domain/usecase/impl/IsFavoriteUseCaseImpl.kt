package uz.gita.dictionaryapp.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import uz.gita.dictionaryapp.domain.repository.FavoriteRepository
import uz.gita.dictionaryapp.domain.usecase.IsFavoriteUseCase
import javax.inject.Inject

class IsFavoriteUseCaseImpl @Inject constructor(
    private val repository: FavoriteRepository
) : IsFavoriteUseCase {
    override fun invoke(english: String): Flow<Boolean> = repository.isFavorite(english)
}
