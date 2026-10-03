package uz.gita.dictionaryapp.domain.usecase.impl

import uz.gita.dictionaryapp.domain.model.Accent
import uz.gita.dictionaryapp.domain.repository.SpeechRepository
import uz.gita.dictionaryapp.domain.usecase.SpeakUseCase
import javax.inject.Inject

class SpeakUseCaseImpl @Inject constructor(
    private val repository: SpeechRepository
) : SpeakUseCase {
    override suspend fun invoke(text: String, accent: Accent?): Result<Unit> = repository.speak(text, accent)
}
