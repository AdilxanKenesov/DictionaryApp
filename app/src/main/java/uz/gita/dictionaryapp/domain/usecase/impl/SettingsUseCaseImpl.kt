package uz.gita.dictionaryapp.domain.usecase.impl

import kotlinx.coroutines.flow.StateFlow
import uz.gita.dictionaryapp.domain.model.Accent
import uz.gita.dictionaryapp.domain.model.AppLanguage
import uz.gita.dictionaryapp.domain.model.AppSettings
import uz.gita.dictionaryapp.domain.model.ThemeMode
import uz.gita.dictionaryapp.domain.repository.SettingsRepository
import uz.gita.dictionaryapp.domain.usecase.SettingsUseCase
import javax.inject.Inject

class SettingsUseCaseImpl @Inject constructor(
    private val repository: SettingsRepository
) : SettingsUseCase {
    override fun getSettings(): StateFlow<AppSettings> = repository.settings

    override fun setThemeMode(mode: ThemeMode) = repository.setThemeMode(mode)

    override fun setLanguage(language: AppLanguage) = repository.setLanguage(language)

    override fun setAccent(accent: Accent) = repository.setAccent(accent)

    override fun setSpeechRate(rate: Float) = repository.setSpeechRate(rate.coerceIn(MIN_RATE, MAX_RATE))

    companion object {
        const val MIN_RATE = 0.5f
        const val MAX_RATE = 1.5f
    }
}
