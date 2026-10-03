package uz.gita.dictionaryapp.presenter.screen.detail

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import org.orbitmvi.orbit.viewmodel.orbitContainer
import uz.gita.dictionaryapp.R
import uz.gita.dictionaryapp.domain.model.Accent
import uz.gita.dictionaryapp.domain.model.SpeechUnavailableException
import uz.gita.dictionaryapp.domain.model.WordDetail
import uz.gita.dictionaryapp.domain.usecase.AddHistoryUseCase
import uz.gita.dictionaryapp.domain.usecase.GetWordDetailUseCase
import uz.gita.dictionaryapp.domain.usecase.IsFavoriteUseCase
import uz.gita.dictionaryapp.domain.usecase.SpeakUseCase
import uz.gita.dictionaryapp.domain.usecase.ToggleFavoriteUseCase
import uz.gita.dictionaryapp.presenter.ui.components.UiMessage
import uz.gita.dictionaryapp.utils.toMessageRes
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getWordDetailUseCase: GetWordDetailUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val addHistoryUseCase: AddHistoryUseCase,
    private val speakUseCase: SpeakUseCase,
    private val directions: DetailContract.Directions
) : ViewModel(), DetailContract.ViewModel {

    override val container = orbitContainer<DetailContract.UiState, DetailContract.SideEffect>(
        DetailContract.UiState()
    )

    private var loadedWord: String? = null
    private var favoriteJob: Job? = null

    override fun onEventDispatcher(intent: DetailContract.Intent) {
        when (intent) {
            is DetailContract.Intent.Load -> load(intent.english)
            DetailContract.Intent.Back -> back()
            DetailContract.Intent.ToggleFavorite -> toggleFavorite()
            is DetailContract.Intent.Speak -> speak(intent.accent)
            is DetailContract.Intent.ToggleExpand -> toggleExpand(intent.type)
            DetailContract.Intent.Copy -> copy()
            DetailContract.Intent.Share -> share()
        }
    }

    // Ekran qayta chizilganda (masalan, burilganda) qayta yuklamaslik uchun
    private fun load(english: String) {
        if (loadedWord == english) return
        loadedWord = english
        observeFavorite(english)
        intent {
            reduce { state.copy(isLoading = true) }
            getWordDetailUseCase(english)
                .onSuccess { detail ->
                    reduce { state.copy(isLoading = false, detail = detail) }
                    if (detail != null) addHistoryUseCase(detail.english)
                }
                .onFailure { e ->
                    reduce { state.copy(isLoading = false) }
                    postSideEffect(DetailContract.SideEffect.ShowMessage(UiMessage(e.toMessageRes())))
                }
        }
    }

    private fun observeFavorite(english: String) {
        favoriteJob?.cancel()
        favoriteJob = intent {
            isFavoriteUseCase(english).collect { isFavorite ->
                reduce { state.copy(isFavorite = isFavorite) }
            }
        }
    }

    private fun back() = intent {
        directions.back()
    }

    private fun toggleFavorite() = intent {
        val english = state.detail?.english ?: return@intent
        toggleFavoriteUseCase(english).onFailure { e ->
            postSideEffect(DetailContract.SideEffect.ShowMessage(UiMessage(e.toMessageRes())))
        }
    }

    private fun speak(accent: Accent) = intent {
        val english = state.detail?.english ?: return@intent
        speakUseCase(english, accent).onFailure { e ->
            if (e is SpeechUnavailableException) {
                postSideEffect(DetailContract.SideEffect.SpeechUnavailable)
            } else {
                postSideEffect(DetailContract.SideEffect.ShowMessage(UiMessage(e.toMessageRes())))
            }
        }
    }

    private fun toggleExpand(type: String) = intent {
        val expanded = if (type in state.expandedTypes) state.expandedTypes - type else state.expandedTypes + type
        reduce { state.copy(expandedTypes = expanded) }
    }

    private fun copy() = intent {
        val detail = state.detail ?: return@intent
        postSideEffect(DetailContract.SideEffect.CopyText(detail.toShareText()))
        postSideEffect(DetailContract.SideEffect.ShowMessage(UiMessage(R.string.copied)))
    }

    private fun share() = intent {
        val detail = state.detail ?: return@intent
        postSideEffect(DetailContract.SideEffect.ShareText(detail.toShareText()))
    }

    private fun WordDetail.toShareText(): String = buildString {
        append(english)
        if (transcript.isNotBlank()) append(" ").append(transcript)
        senses.groupBy { it.type }.forEach { (type, senses) ->
            append("\n")
            if (type.isNotBlank()) append(type).append(": ")
            append(senses.joinToString(", ") { it.uzbek })
        }
    }
}
