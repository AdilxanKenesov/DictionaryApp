package uz.gita.dictionaryapp.presenter.screen.practice

import org.orbitmvi.orbit.OrbitContainerHost
import uz.gita.dictionaryapp.domain.model.PracticeSource
import uz.gita.dictionaryapp.domain.model.PracticeSummary
import uz.gita.dictionaryapp.domain.model.QuizMode
import uz.gita.dictionaryapp.presenter.ui.components.UiMessage

interface PracticeContract {

    interface ViewModel : OrbitContainerHost<UiState, UiState, SideEffect> {
        fun onEventDispatcher(intent: Intent)
    }

    sealed interface Intent {
        data class SelectSource(val source: PracticeSource) : Intent
        data class SelectQuizMode(val mode: QuizMode) : Intent
        data object StartFlashcards : Intent
        data object StartQuiz : Intent
    }

    sealed interface SideEffect {
        data class ShowMessage(val message: UiMessage) : SideEffect
    }

    data class UiState(
        val source: PracticeSource = PracticeSource.RANDOM,
        val quizMode: QuizMode = QuizMode.EN_UZ,
        val summary: PracticeSummary = PracticeSummary()
    )

    interface Directions {
        fun navigateToFlashcards(source: PracticeSource)
        fun navigateToQuiz(source: PracticeSource, mode: QuizMode)
    }
}
