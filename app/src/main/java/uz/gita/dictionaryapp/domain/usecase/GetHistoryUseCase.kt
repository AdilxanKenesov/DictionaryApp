package uz.gita.dictionaryapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.gita.dictionaryapp.domain.model.WordItem

interface GetHistoryUseCase {
    operator fun invoke(): Flow<Result<List<WordItem>>>
}
