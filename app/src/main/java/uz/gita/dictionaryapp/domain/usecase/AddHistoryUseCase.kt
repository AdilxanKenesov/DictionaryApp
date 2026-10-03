package uz.gita.dictionaryapp.domain.usecase

interface AddHistoryUseCase {
    suspend operator fun invoke(english: String): Result<Unit>
}
