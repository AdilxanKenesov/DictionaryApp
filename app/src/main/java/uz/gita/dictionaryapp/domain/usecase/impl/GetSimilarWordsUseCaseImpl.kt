package uz.gita.dictionaryapp.domain.usecase.impl

import uz.gita.dictionaryapp.domain.repository.DictionaryRepository
import uz.gita.dictionaryapp.domain.usecase.GetSimilarWordsUseCase
import javax.inject.Inject

class GetSimilarWordsUseCaseImpl @Inject constructor(
    private val repository: DictionaryRepository
) : GetSimilarWordsUseCase {
    override suspend fun invoke(query: String): Result<List<String>> {
        val text = query.trim()
        if (text.length < MIN_LENGTH) return Result.success(emptyList())
        return repository.getSimilar(text)
    }

    companion object {
        const val MIN_LENGTH = 2
    }
}
