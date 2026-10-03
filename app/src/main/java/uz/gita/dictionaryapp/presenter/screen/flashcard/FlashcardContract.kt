package uz.gita.dictionaryapp.presenter.screen.flashcard

import org.orbitmvi.orbit.OrbitContainerHost
import uz.gita.dictionaryapp.domain.model.PracticeSource
import uz.gita.dictionaryapp.domain.model.WordItem
import uz.gita.dictionaryapp.presenter.ui.components.UiMessage

interface FlashcardContract {

    interface ViewModel : OrbitContainerHost<UiState, UiState, SideEffect> {
        fun onEventDispatcher(intent: Intent)
    }

    sealed interface Intent {
        data class Load(val source: PracticeSource) : Intent
        data object Flip : Intent
        data object Know : Intent
        data object Repeat : Intent
        data object Speak : Intent
        data object Restart : Intent
        data object Close : Intent
    }

    sealed interface SideEffect {
        data class ShowMessage(val message: UiMessage) : SideEffect
    }

    data class UiState(
        val isLoading: Boolean = true,
        val queue: List<WordItem> = emptyList(),
        val total: Int = 0,
        val known: Int = 0,
        val isFlipped: Boolean = false
    ) {
        val current: WordItem? get() = queue.firstOrNull()
        val isFinished: Boolean get() = !isLoading && total > 0 && queue.isEmpty()
    }

    interface Directions {
        fun back()
    }
}
