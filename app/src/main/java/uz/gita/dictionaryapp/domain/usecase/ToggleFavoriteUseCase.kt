package uz.gita.dictionaryapp.domain.usecase

interface ToggleFavoriteUseCase {
    suspend operator fun invoke(english: String): Result<Boolean>
}
