package uz.gita.dictionaryapp.domain.usecase

import uz.gita.dictionaryapp.domain.model.SearchDirection
import uz.gita.dictionaryapp.domain.model.WordItem

interface SearchWordsUseCase {
    suspend operator fun invoke(query: String, direction: SearchDirection): Result<List<WordItem>>
}
