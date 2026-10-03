package uz.gita.dictionaryapp.data.repository_impl

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import uz.gita.dictionaryapp.data.storage.SettingsManager
import uz.gita.dictionaryapp.domain.model.Accent
import uz.gita.dictionaryapp.domain.model.AppLanguage
import uz.gita.dictionaryapp.domain.model.AppSettings
import uz.gita.dictionaryapp.domain.model.ThemeMode
import uz.gita.dictionaryapp.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

// Sozlamalar StateFlow orqali beriladi: mavzu o'zgarsa butun ilova darhol yangilanadi
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val manager: SettingsManager
) : SettingsRepository {

    private val _settings = MutableStateFlow(
        AppSettings(
            themeMode = manager.themeMode,
            language = manager.language,
            accent = manager.accent,
            speechRate = manager.speechRate
        )
    )
    override val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    override fun setThemeMode(mode: ThemeMode) {
        manager.themeMode = mode
        _settings.update { it.copy(themeMode = mode) }
    }

    override fun setLanguage(language: AppLanguage) {
        manager.language = language
        _settings.update { it.copy(language = language) }
    }

    override fun setAccent(accent: Accent) {
        manager.accent = accent
        _settings.update { it.copy(accent = accent) }
    }

    override fun setSpeechRate(rate: Float) {
        manager.speechRate = rate
        _settings.update { it.copy(speechRate = rate) }
    }
}
