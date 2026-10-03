package uz.gita.dictionaryapp.presenter.screen.practice

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import uz.gita.dictionaryapp.R
import uz.gita.dictionaryapp.domain.model.PracticeSource
import uz.gita.dictionaryapp.domain.model.QuizMode
import uz.gita.dictionaryapp.domain.usecase.GetPracticeSummaryUseCase
import uz.gita.dictionaryapp.domain.usecase.impl.GetPracticeWordsUseCaseImpl
import uz.gita.dictionaryapp.presenter.ui.components.UiMessage
import javax.inject.Inject

@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val getPracticeSummaryUseCase: GetPracticeSummaryUseCase,
    private val directions: PracticeContract.Directions
) : ViewModel(), PracticeContract.ViewModel {

    override val container = orbitContainer<PracticeContract.UiState, PracticeContract.SideEffect>(
        PracticeContract.UiState()
    )

    // Foydalanuvchi o'zi manba tanlamaguncha: sevimlilar yetarli bo'lsa ular tanlanadi
    private var sourceChosenByUser = false

    init {
        observeSummary()
    }

    override fun onEventDispatcher(intent: PracticeContract.Intent) {
        when (intent) {
            is PracticeContract.Intent.SelectSource -> selectSource(intent.source)
            is PracticeContract.Intent.SelectQuizMode -> selectQuizMode(intent.mode)
            PracticeContract.Intent.StartFlashcards -> startFlashcards()
            PracticeContract.Intent.StartQuiz -> startQuiz()
        }
    }

    private fun selectSource(source: PracticeSource) = intent {
        sourceChosenByUser = true
        reduce { state.copy(source = source) }
    }

    private fun selectQuizMode(mode: QuizMode) = intent {
        reduce { state.copy(quizMode = mode) }
    }

    private fun startFlashcards() = intent {
        if (hasEnoughWords(state)) directions.navigateToFlashcards(state.source)
        else postSideEffect(notEnoughWordsMessage())
    }

    private fun startQuiz() = intent {
        if (hasEnoughWords(state)) directions.navigateToQuiz(state.source, state.quizMode)
        else postSideEffect(notEnoughWordsMessage())
    }

    private fun hasEnoughWords(state: PracticeContract.UiState): Boolean =
        state.source == PracticeSource.RANDOM ||
                state.summary.favoritesCount >= GetPracticeWordsUseCaseImpl.MIN_FAVORITES

    private fun notEnoughWordsMessage() = PracticeContract.SideEffect.ShowMessage(
        UiMessage(R.string.not_enough_words, listOf(GetPracticeWordsUseCaseImpl.MIN_FAVORITES))
    )

    private fun observeSummary() = intent {
        getPracticeSummaryUseCase().collect { result ->
            result.onSuccess { summary ->
                reduce {
                    val source = if (sourceChosenByUser) state.source
                    else if (summary.favoritesCount >= GetPracticeWordsUseCaseImpl.MIN_FAVORITES) PracticeSource.FAVORITES
                    else PracticeSource.RANDOM
                    state.copy(summary = summary, source = source)
                }
            }
        }
    }
}
