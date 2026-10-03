package uz.gita.dictionaryapp.domain.usecase.impl

import uz.gita.dictionaryapp.domain.model.SearchDirection
import uz.gita.dictionaryapp.domain.model.WordItem
import uz.gita.dictionaryapp.domain.repository.DictionaryRepository
import uz.gita.dictionaryapp.domain.usecase.SearchWordsUseCase
import javax.inject.Inject

class SearchWordsUseCaseImpl @Inject constructor(
    private val repository: DictionaryRepository
) : SearchWordsUseCase {
    override suspend fun invoke(query: String, direction: SearchDirection): Result<List<WordItem>> {
        val text = query.trim()
        if (text.isEmpty()) return Result.success(emptyList())
        return repository.search(text, direction)
    }
}
