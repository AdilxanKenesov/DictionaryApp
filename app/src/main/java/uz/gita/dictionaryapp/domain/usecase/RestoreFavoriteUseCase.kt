package uz.gita.dictionaryapp.domain.usecase

interface RestoreFavoriteUseCase {
    suspend operator fun invoke(english: String, addedAt: Long): Result<Unit>
}
