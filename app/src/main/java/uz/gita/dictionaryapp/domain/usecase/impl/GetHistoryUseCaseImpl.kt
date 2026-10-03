package uz.gita.dictionaryapp.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import uz.gita.dictionaryapp.domain.model.WordItem
import uz.gita.dictionaryapp.domain.repository.HistoryRepository
import uz.gita.dictionaryapp.domain.usecase.GetHistoryUseCase
import javax.inject.Inject

class GetHistoryUseCaseImpl @Inject constructor(
    private val repository: HistoryRepository
) : GetHistoryUseCase {
    override fun invoke(): Flow<Result<List<WordItem>>> = repository.getRecent()
}
