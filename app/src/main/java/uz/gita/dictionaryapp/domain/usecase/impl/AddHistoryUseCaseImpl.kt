package uz.gita.dictionaryapp.domain.usecase.impl

import uz.gita.dictionaryapp.domain.repository.HistoryRepository
import uz.gita.dictionaryapp.domain.usecase.AddHistoryUseCase
import javax.inject.Inject

class AddHistoryUseCaseImpl @Inject constructor(
    private val repository: HistoryRepository
) : AddHistoryUseCase {
    override suspend fun invoke(english: String): Result<Unit> = repository.add(english)
}
