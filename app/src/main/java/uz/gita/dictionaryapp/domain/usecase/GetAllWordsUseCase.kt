package uz.gita.dictionaryapp.domain.usecase

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import uz.gita.dictionaryapp.domain.model.WordItem

interface GetAllWordsUseCase {
    operator fun invoke(): Flow<PagingData<WordItem>>
}
