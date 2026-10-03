package uz.gita.dictionaryapp.domain.usecase.impl

import uz.gita.dictionaryapp.domain.repository.HistoryRepository
import uz.gita.dictionaryapp.domain.usecase.ClearHistoryUseCase
import javax.inject.Inject

class ClearHistoryUseCaseImpl @Inject constructor(
    private val repository: HistoryRepository
) : ClearHistoryUseCase {
    override suspend fun invoke(): Result<Unit> = repository.clear()
}
