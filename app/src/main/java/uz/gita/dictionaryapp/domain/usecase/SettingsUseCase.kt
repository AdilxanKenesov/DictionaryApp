package uz.gita.dictionaryapp.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import uz.gita.dictionaryapp.domain.model.Accent
import uz.gita.dictionaryapp.domain.model.AppLanguage
import uz.gita.dictionaryapp.domain.model.AppSettings
import uz.gita.dictionaryapp.domain.model.ThemeMode

interface SettingsUseCase {
    fun getSettings(): StateFlow<AppSettings>

    fun setThemeMode(mode: ThemeMode)

    fun setLanguage(language: AppLanguage)

    fun setAccent(accent: Accent)

    fun setSpeechRate(rate: Float)
}
