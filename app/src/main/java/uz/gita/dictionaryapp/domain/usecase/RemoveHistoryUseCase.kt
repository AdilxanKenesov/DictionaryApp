package uz.gita.dictionaryapp.domain.usecase

interface RemoveHistoryUseCase {
    suspend operator fun invoke(english: String): Result<Unit>
}
