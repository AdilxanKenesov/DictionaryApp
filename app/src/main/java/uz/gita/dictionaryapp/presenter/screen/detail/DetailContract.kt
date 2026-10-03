package uz.gita.dictionaryapp.presenter.screen.detail

import org.orbitmvi.orbit.OrbitContainerHost
import uz.gita.dictionaryapp.domain.model.Accent
import uz.gita.dictionaryapp.domain.model.WordDetail
import uz.gita.dictionaryapp.presenter.ui.components.UiMessage

interface DetailContract {

    interface ViewModel : OrbitContainerHost<UiState, UiState, SideEffect> {
        fun onEventDispatcher(intent: Intent)
    }

    sealed interface Intent {
        data class Load(val english: String) : Intent
        data object Back : Intent
        data object ToggleFavorite : Intent
        data class Speak(val accent: Accent) : Intent
        data class ToggleExpand(val type: String) : Intent
        data object Copy : Intent
        data object Share : Intent
    }

    sealed interface SideEffect {
        data class ShowMessage(val message: UiMessage) : SideEffect
        data object SpeechUnavailable : SideEffect
        data class CopyText(val text: String) : SideEffect
        data class ShareText(val text: String) : SideEffect
    }

    data class UiState(
        val isLoading: Boolean = true,
        val detail: WordDetail? = null,
        val isFavorite: Boolean = false,
        val expandedTypes: Set<String> = emptySet()
    )

    interface Directions {
        fun back()
    }
}
