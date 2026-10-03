package uz.gita.dictionaryapp.presenter.screen.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.blockingIntent
import org.orbitmvi.orbit.viewmodel.orbitContainer
import uz.gita.dictionaryapp.domain.model.Accent
import uz.gita.dictionaryapp.domain.model.AppLanguage
import uz.gita.dictionaryapp.domain.model.ThemeMode
import uz.gita.dictionaryapp.domain.usecase.SettingsUseCase
import uz.gita.dictionaryapp.domain.usecase.SpeakUseCase
import uz.gita.dictionaryapp.presenter.ui.components.UiMessage
import uz.gita.dictionaryapp.utils.toMessageRes
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val speakUseCase: SpeakUseCase
) : ViewModel(), SettingsContract.ViewModel {

    override val container = orbitContainer<SettingsContract.UiState, SettingsContract.SideEffect>(
        SettingsContract.UiState(settings = settingsUseCase.getSettings().value)
    )

    init {
        observeSettings()
    }

    override fun onEventDispatcher(intent: SettingsContract.Intent) {
        when (intent) {
            is SettingsContract.Intent.SetDarkTheme -> setDarkTheme(intent.enabled)
            is SettingsContract.Intent.SetLanguage -> setLanguage(intent.language)
            is SettingsContract.Intent.SetAccent -> setAccent(intent.accent)
            is SettingsContract.Intent.SetSpeechRate -> setSpeechRate(intent.rate)
        }
    }

    private fun setDarkTheme(enabled: Boolean) = intent {
        settingsUseCase.setThemeMode(if (enabled) ThemeMode.DARK else ThemeMode.LIGHT)
    }

    // MainActivity tilni kuzatadi va o'zini qayta yaratadi
    private fun setLanguage(language: AppLanguage) = intent {
        settingsUseCase.setLanguage(language)
    }

    // Aksent tanlanganda darhol eshittirib beramiz
    private fun setAccent(accent: Accent) = intent {
        settingsUseCase.setAccent(accent)
        speakUseCase(SAMPLE_TEXT).onFailure { e ->
            postSideEffect(SettingsContract.SideEffect.ShowMessage(UiMessage(e.toMessageRes())))
        }
    }

    // Slayder uchun blockingIntent — qiymat sakramasligi uchun
    private fun setSpeechRate(rate: Float) = blockingIntent {
        settingsUseCase.setSpeechRate(rate)
    }

    private fun observeSettings() = intent {
        settingsUseCase.getSettings().collect { settings ->
            reduce { state.copy(settings = settings) }
        }
    }

    private companion object {
        const val SAMPLE_TEXT = "Hello! How are you today?"
    }
}
