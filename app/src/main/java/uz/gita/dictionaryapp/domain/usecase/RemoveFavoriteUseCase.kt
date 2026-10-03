package uz.gita.dictionaryapp.domain.usecase

interface RemoveFavoriteUseCase {
    // Qaytarish (undo) uchun qo'shilgan vaqtini qaytaradi
    suspend operator fun invoke(english: String): Result<Long?>
}
