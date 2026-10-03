package uz.gita.dictionaryapp.domain.usecase.impl

import uz.gita.dictionaryapp.domain.repository.FavoriteRepository
import uz.gita.dictionaryapp.domain.usecase.ToggleFavoriteUseCase
import javax.inject.Inject

class ToggleFavoriteUseCaseImpl @Inject constructor(
    private val repository: FavoriteRepository
) : ToggleFavoriteUseCase {
    override suspend fun invoke(english: String): Result<Boolean> = repository.toggle(english)
}
