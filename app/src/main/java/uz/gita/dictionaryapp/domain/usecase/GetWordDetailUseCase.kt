package uz.gita.dictionaryapp.domain.usecase

import uz.gita.dictionaryapp.domain.model.WordDetail

interface GetWordDetailUseCase {
    suspend operator fun invoke(english: String): Result<WordDetail?>
}
