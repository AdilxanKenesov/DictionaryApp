package uz.gita.dictionaryapp.domain.usecase.impl

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import uz.gita.dictionaryapp.domain.model.WordItem
import uz.gita.dictionaryapp.domain.repository.DictionaryRepository
import uz.gita.dictionaryapp.domain.usecase.GetAllWordsUseCase
import javax.inject.Inject

class GetAllWordsUseCaseImpl @Inject constructor(
    private val repository: DictionaryRepository
) : GetAllWordsUseCase {
    override fun invoke(): Flow<PagingData<WordItem>> = repository.getAllWords()
}
