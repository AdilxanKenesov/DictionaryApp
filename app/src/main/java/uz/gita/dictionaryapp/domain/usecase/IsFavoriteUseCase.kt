package uz.gita.dictionaryapp.domain.usecase

import kotlinx.coroutines.flow.Flow

interface IsFavoriteUseCase {
    operator fun invoke(english: String): Flow<Boolean>
}
