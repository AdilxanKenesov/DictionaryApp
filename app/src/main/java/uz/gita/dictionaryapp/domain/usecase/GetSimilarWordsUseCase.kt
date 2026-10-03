package uz.gita.dictionaryapp.domain.usecase

interface GetSimilarWordsUseCase {
    suspend operator fun invoke(query: String): Result<List<String>>
}
