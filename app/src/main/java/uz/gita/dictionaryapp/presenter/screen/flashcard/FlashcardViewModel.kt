package uz.gita.dictionaryapp.presenter.screen.flashcard

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import uz.gita.dictionaryapp.R
import uz.gita.dictionaryapp.domain.model.NotEnoughWordsException
import uz.gita.dictionaryapp.domain.model.PracticeSource
import uz.gita.dictionaryapp.domain.usecase.GetPracticeWordsUseCase
import uz.gita.dictionaryapp.domain.usecase.SpeakUseCase
import uz.gita.dictionaryapp.presenter.ui.components.UiMessage
import uz.gita.dictionaryapp.utils.toMessageRes
import javax.inject.Inject

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    private val getPracticeWordsUseCase: GetPracticeWordsUseCase,
    private val speakUseCase: SpeakUseCase,
    private val directions: FlashcardContract.Directions
) : ViewModel(), FlashcardContract.ViewModel {

    override val container = orbitContainer<FlashcardContract.UiState, FlashcardContract.SideEffect>(
        FlashcardContract.UiState()
    )

    private var source: PracticeSource? = null

    override fun onEventDispatcher(intent: FlashcardContract.Intent) {
        when (intent) {
            is FlashcardContract.Intent.Load -> load(intent.source)
            FlashcardContract.Intent.Flip -> flip()
            FlashcardContract.Intent.Know -> know()
            FlashcardContract.Intent.Repeat -> repeat()
            FlashcardContract.Intent.Speak -> speak()
            FlashcardContract.Intent.Restart -> source?.let { start(it) }
            FlashcardContract.Intent.Close -> close()
        }
    }

    private fun load(source: PracticeSource) {
        if (this.source != null) return
        this.source = source
        start(source)
    }

    private fun start(source: PracticeSource) = intent {
        reduce { FlashcardContract.UiState(isLoading = true) }
        getPracticeWordsUseCase(source, DECK_SIZE)
            .onSuccess { words ->
                reduce { FlashcardContract.UiState(isLoading = false, queue = words, total = words.size) }
            }
            .onFailure { e ->
                val message = if (e is NotEnoughWordsException) UiMessage(R.string.not_enough_words, listOf(e.required))
                else UiMessage(e.toMessageRes())
                postSideEffect(FlashcardContract.SideEffect.ShowMessage(message))
                directions.back()
            }
    }

    private fun flip() = intent {
        reduce { state.copy(isFlipped = !state.isFlipped) }
    }

    private fun know() = intent {
        if (state.queue.isEmpty()) return@intent
        reduce { state.copy(queue = state.queue.drop(1), known = state.known + 1, isFlipped = false) }
    }

    // Bilinmagan so'z navbat oxiriga qaytadi
    private fun repeat() = intent {
        val current = state.current ?: return@intent
        reduce { state.copy(queue = state.queue.drop(1) + current, isFlipped = false) }
    }

    private fun speak() = intent {
        val current = state.current ?: return@intent
        speakUseCase(current.english).onFailure { e ->
            postSideEffect(FlashcardContract.SideEffect.ShowMessage(UiMessage(e.toMessageRes())))
        }
    }

    private fun close() = intent {
        directions.back()
    }

    private companion object {
        const val DECK_SIZE = 20
    }
}
