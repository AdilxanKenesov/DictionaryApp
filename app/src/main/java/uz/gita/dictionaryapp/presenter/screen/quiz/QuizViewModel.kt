package uz.gita.dictionaryapp.presenter.screen.quiz

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.viewmodel.orbitContainer
import uz.gita.dictionaryapp.R
import uz.gita.dictionaryapp.domain.model.NotEnoughWordsException
import uz.gita.dictionaryapp.domain.model.PracticeSource
import uz.gita.dictionaryapp.domain.model.QuizMode
import uz.gita.dictionaryapp.domain.model.WrongAnswer
import uz.gita.dictionaryapp.domain.usecase.GenerateQuizUseCase
import uz.gita.dictionaryapp.domain.usecase.SaveQuizResultUseCase
import uz.gita.dictionaryapp.domain.usecase.SpeakUseCase
import uz.gita.dictionaryapp.presenter.ui.components.UiMessage
import uz.gita.dictionaryapp.utils.toMessageRes
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val generateQuizUseCase: GenerateQuizUseCase,
    private val saveQuizResultUseCase: SaveQuizResultUseCase,
    private val speakUseCase: SpeakUseCase,
    private val directions: QuizContract.Directions
) : ViewModel(), QuizContract.ViewModel {

    override val container = orbitContainer<QuizContract.UiState, QuizContract.SideEffect>(
        QuizContract.UiState()
    )

    private var source: PracticeSource? = null

    override fun onEventDispatcher(intent: QuizContract.Intent) {
        when (intent) {
            is QuizContract.Intent.Load -> load(intent.source, intent.mode)
            is QuizContract.Intent.Select -> select(intent.option)
            QuizContract.Intent.Speak -> speak()
            QuizContract.Intent.Close -> close()
        }
    }

    private fun load(source: PracticeSource, mode: QuizMode) {
        if (this.source != null) return
        this.source = source
        intent {
            reduce { state.copy(isLoading = true, mode = mode) }
            generateQuizUseCase(source, mode)
                .onSuccess { questions ->
                    reduce { state.copy(isLoading = false, questions = questions) }
                }
                .onFailure { e ->
                    val message = if (e is NotEnoughWordsException) UiMessage(R.string.not_enough_words, listOf(e.required))
                    else UiMessage(e.toMessageRes())
                    postSideEffect(QuizContract.SideEffect.ShowMessage(message))
                    directions.back()
                }
        }
    }

    // Javob rangini ko'rsatib, 1 soniyadan keyin keyingi savolga o'tamiz
    private fun select(option: String) = intent {
        val question = state.current ?: return@intent
        if (state.selected != null) return@intent

        val isCorrect = option == question.answer
        reduce {
            state.copy(
                selected = option,
                score = if (isCorrect) state.score + 1 else state.score,
                wrongAnswers = if (isCorrect) state.wrongAnswers else state.wrongAnswers + WrongAnswer(
                    english = question.english,
                    prompt = question.prompt,
                    chosen = option,
                    answer = question.answer
                )
            )
        }
        delay(ANSWER_DELAY)

        if (state.index + 1 < state.questions.size) {
            reduce { state.copy(index = state.index + 1, selected = null) }
        } else {
            saveQuizResultUseCase(state.score, state.questions.size, state.mode)
            directions.navigateToResult(
                score = state.score,
                total = state.questions.size,
                wrongAnswers = state.wrongAnswers,
                source = source ?: PracticeSource.RANDOM,
                mode = state.mode
            )
        }
    }

    private fun speak() = intent {
        val question = state.current ?: return@intent
        speakUseCase(question.english).onFailure { e ->
            postSideEffect(QuizContract.SideEffect.ShowMessage(UiMessage(e.toMessageRes())))
        }
    }

    private fun close() = intent {
        directions.back()
    }

    private companion object {
        const val ANSWER_DELAY = 1_000L
    }
}
