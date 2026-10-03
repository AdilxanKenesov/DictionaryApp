package uz.gita.dictionaryapp.domain.usecase

import uz.gita.dictionaryapp.domain.model.PracticeSource
import uz.gita.dictionaryapp.domain.model.WordItem

interface GetPracticeWordsUseCase {
    suspend operator fun invoke(source: PracticeSource, limit: Int): Result<List<WordItem>>
}
