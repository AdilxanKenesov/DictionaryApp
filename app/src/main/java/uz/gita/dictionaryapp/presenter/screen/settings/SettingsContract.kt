package uz.gita.dictionaryapp.presenter.screen.settings

import org.orbitmvi.orbit.OrbitContainerHost
import uz.gita.dictionaryapp.domain.model.Accent
import uz.gita.dictionaryapp.domain.model.AppLanguage
import uz.gita.dictionaryapp.domain.model.AppSettings
import uz.gita.dictionaryapp.presenter.ui.components.UiMessage

interface SettingsContract {

    interface ViewModel : OrbitContainerHost<UiState, UiState, SideEffect> {
        fun onEventDispatcher(intent: Intent)
    }

    sealed interface Intent {
        data class SetDarkTheme(val enabled: Boolean) : Intent
        data class SetLanguage(val language: AppLanguage) : Intent
        data class SetAccent(val accent: Accent) : Intent
        data class SetSpeechRate(val rate: Float) : Intent
    }

    sealed interface SideEffect {
        data class ShowMessage(val message: UiMessage) : SideEffect
    }

    data class UiState(
        val settings: AppSettings = AppSettings()
    )
}
