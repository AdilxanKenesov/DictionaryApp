package uz.gita.dictionaryapp.domain.usecase.impl

import uz.gita.dictionaryapp.domain.repository.FavoriteRepository
import uz.gita.dictionaryapp.domain.usecase.RestoreFavoriteUseCase
import javax.inject.Inject

class RestoreFavoriteUseCaseImpl @Inject constructor(
    private val repository: FavoriteRepository
) : RestoreFavoriteUseCase {
    override suspend fun invoke(english: String, addedAt: Long): Result<Unit> = repository.restore(english, addedAt)
}
