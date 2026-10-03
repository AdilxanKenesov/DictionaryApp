package uz.gita.dictionaryapp.data.repository_impl

import uz.gita.dictionaryapp.data.tts.SpeechManager
import uz.gita.dictionaryapp.domain.model.Accent
import uz.gita.dictionaryapp.domain.repository.SettingsRepository
import uz.gita.dictionaryapp.domain.repository.SpeechRepository
import uz.gita.dictionaryapp.utils.resultOf
import java.util.Locale
import javax.inject.Inject

class SpeechRepositoryImpl @Inject constructor(
    private val speechManager: SpeechManager,
    private val settingsRepository: SettingsRepository
) : SpeechRepository {

    override suspend fun speak(text: String, accent: Accent?): Result<Unit> = resultOf {
        val settings = settingsRepository.settings.value
        val locale = when (accent ?: settings.accent) {
            Accent.US -> Locale.US
            Accent.UK -> Locale.UK
        }
        speechManager.speak(text, locale, settings.speechRate)
    }
}
