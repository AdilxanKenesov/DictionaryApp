package uz.gita.dictionaryapp.domain.usecase.impl

import uz.gita.dictionaryapp.domain.model.WordDetail
import uz.gita.dictionaryapp.domain.repository.DictionaryRepository
import uz.gita.dictionaryapp.domain.usecase.GetWordDetailUseCase
import javax.inject.Inject

class GetWordDetailUseCaseImpl @Inject constructor(
    private val repository: DictionaryRepository
) : GetWordDetailUseCase {
    override suspend fun invoke(english: String): Result<WordDetail?> = repository.getWordDetail(english)
}
