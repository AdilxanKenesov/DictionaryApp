package uz.gita.dictionaryapp.domain.usecase.impl

import uz.gita.dictionaryapp.domain.repository.FavoriteRepository
import uz.gita.dictionaryapp.domain.usecase.RemoveFavoriteUseCase
import javax.inject.Inject

class RemoveFavoriteUseCaseImpl @Inject constructor(
    private val repository: FavoriteRepository
) : RemoveFavoriteUseCase {
    override suspend fun invoke(english: String): Result<Long?> = repository.remove(english)
}
