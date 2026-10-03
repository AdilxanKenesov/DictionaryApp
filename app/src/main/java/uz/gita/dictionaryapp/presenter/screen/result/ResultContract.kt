package uz.gita.dictionaryapp.presenter.screen.result

import org.orbitmvi.orbit.OrbitContainerHost
import uz.gita.dictionaryapp.domain.model.PracticeSource
import uz.gita.dictionaryapp.domain.model.QuizMode

interface ResultContract {

    interface ViewModel : OrbitContainerHost<UiState, UiState, SideEffect> {
        fun onEventDispatcher(intent: Intent)
    }

    sealed interface Intent {
        data class OpenWord(val english: String) : Intent
        data class Retry(val source: PracticeSource, val mode: QuizMode) : Intent
        data object Home : Intent
    }

    sealed interface SideEffect

    // Natija ma'lumotlari ekran parametrlaridan keladi
    data object UiState

    interface Directions {
        fun navigateToDetail(english: String)
        fun retry(source: PracticeSource, mode: QuizMode)
        fun back()
    }
}
