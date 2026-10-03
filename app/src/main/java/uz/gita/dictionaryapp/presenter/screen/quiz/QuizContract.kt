package uz.gita.dictionaryapp.presenter.screen.quiz

import org.orbitmvi.orbit.OrbitContainerHost
import uz.gita.dictionaryapp.domain.model.PracticeSource
import uz.gita.dictionaryapp.domain.model.QuizMode
import uz.gita.dictionaryapp.domain.model.QuizQuestion
import uz.gita.dictionaryapp.domain.model.WrongAnswer
import uz.gita.dictionaryapp.presenter.ui.components.UiMessage

interface QuizContract {

    interface ViewModel : OrbitContainerHost<UiState, UiState, SideEffect> {
        fun onEventDispatcher(intent: Intent)
    }

    sealed interface Intent {
        data class Load(val source: PracticeSource, val mode: QuizMode) : Intent
        data class Select(val option: String) : Intent
        data object Speak : Intent
        data object Close : Intent
    }

    sealed interface SideEffect {
        data class ShowMessage(val message: UiMessage) : SideEffect
    }

    data class UiState(
        val isLoading: Boolean = true,
        val mode: QuizMode = QuizMode.EN_UZ,
        val questions: List<QuizQuestion> = emptyList(),
        val index: Int = 0,
        val score: Int = 0,
        val selected: String? = null,
        val wrongAnswers: List<WrongAnswer> = emptyList()
    ) {
        val current: QuizQuestion? get() = questions.getOrNull(index)
    }

    interface Directions {
        fun back()
        fun navigateToResult(
            score: Int,
            total: Int,
            wrongAnswers: List<WrongAnswer>,
            source: PracticeSource,
            mode: QuizMode
        )
    }
}
