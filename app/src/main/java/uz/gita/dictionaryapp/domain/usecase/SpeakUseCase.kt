package uz.gita.dictionaryapp.domain.usecase

import uz.gita.dictionaryapp.domain.model.Accent

interface SpeakUseCase {
    suspend operator fun invoke(text: String, accent: Accent? = null): Result<Unit>
}
