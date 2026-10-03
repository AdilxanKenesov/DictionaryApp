package uz.gita.dictionaryapp.domain.usecase.impl

import uz.gita.dictionaryapp.domain.repository.HistoryRepository
import uz.gita.dictionaryapp.domain.usecase.RemoveHistoryUseCase
import javax.inject.Inject

class RemoveHistoryUseCaseImpl @Inject constructor(
    private val repository: HistoryRepository
) : RemoveHistoryUseCase {
    override suspend fun invoke(english: String): Result<Unit> = repository.remove(english)
}
