package uz.gita.dictionaryapp.domain.usecase

interface ClearHistoryUseCase {
    suspend operator fun invoke(): Result<Unit>
}
