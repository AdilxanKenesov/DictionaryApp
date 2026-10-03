package uz.gita.dictionaryapp.presenter.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.orbitmvi.orbit.blockingIntent
import org.orbitmvi.orbit.viewmodel.orbitContainer
import uz.gita.dictionaryapp.domain.model.SearchDirection
import uz.gita.dictionaryapp.domain.usecase.ClearHistoryUseCase
import uz.gita.dictionaryapp.domain.usecase.GetAllWordsUseCase
import uz.gita.dictionaryapp.domain.usecase.GetHistoryUseCase
import uz.gita.dictionaryapp.domain.usecase.GetSimilarWordsUseCase
import uz.gita.dictionaryapp.domain.usecase.GetWordOfDayUseCase
import uz.gita.dictionaryapp.domain.usecase.RemoveHistoryUseCase
import uz.gita.dictionaryapp.domain.usecase.SearchWordsUseCase
import uz.gita.dictionaryapp.domain.usecase.SpeakUseCase
import uz.gita.dictionaryapp.presenter.ui.components.UiMessage
import uz.gita.dictionaryapp.utils.toMessageRes
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchWordsUseCase: SearchWordsUseCase,
    getAllWordsUseCase: GetAllWordsUseCase,
    private val getSimilarWordsUseCase: GetSimilarWordsUseCase,
    private val getWordOfDayUseCase: GetWordOfDayUseCase,
    private val getHistoryUseCase: GetHistoryUseCase,
    private val removeHistoryUseCase: RemoveHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase,
    private val speakUseCase: SpeakUseCase,
    private val directions: SearchContract.Directions
) : ViewModel(), SearchContract.ViewModel {

    override val container = orbitContainer<SearchContract.UiState, SearchContract.SideEffect>(
        SearchContract.UiState()
    )

    // Harf o'zgargan joyga sarlavha qo'yamiz: A, B, C ...
    override val words: Flow<PagingData<SearchContract.ListItem>> = getAllWordsUseCase()
        .map { paging ->
            paging
                .map { SearchContract.ListItem.Word(it) }
                .insertSeparators { before: SearchContract.ListItem.Word?, after: SearchContract.ListItem.Word? ->
                    val letter = after?.word?.english?.firstLetter() ?: return@insertSeparators null
                    if (before?.word?.english?.firstLetter() == letter) null
                    else SearchContract.ListItem.Header(letter, key = "header_${after.word.english}")
                }
        }
        .cachedIn(viewModelScope)

    private var searchJob: Job? = null

    init {
        loadWordOfDay()
        observeHistory()
    }

    override fun onEventDispatcher(intent: SearchContract.Intent) {
        when (intent) {
            is SearchContract.Intent.QueryChanged -> changeQuery(intent.query)
            SearchContract.Intent.ToggleDirection -> toggleDirection()
            is SearchContract.Intent.OpenWord -> openWord(intent.english)
            is SearchContract.Intent.SelectSuggestion -> changeQuery(intent.word)
            SearchContract.Intent.SpeakWordOfDay -> speakWordOfDay()
            is SearchContract.Intent.RemoveHistory -> removeHistory(intent.english)
            SearchContract.Intent.ClearHistory -> clearHistory()
        }
    }

    // TextField uchun blockingIntent — kursor sakramasligi uchun
    private fun changeQuery(query: String) = blockingIntent {
        reduce { state.copy(query = query, isSearching = query.isNotBlank()) }
        search(query, state.direction)
    }

    private fun toggleDirection() = intent {
        val direction = when (state.direction) {
            SearchDirection.EN_UZ -> SearchDirection.UZ_EN
            SearchDirection.UZ_EN -> SearchDirection.EN_UZ
        }
        reduce { state.copy(direction = direction, isSearching = state.query.isNotBlank()) }
        search(state.query, direction)
    }

    // Har harf yozilganda emas, 250 ms to'xtagandan keyin qidiramiz
    private fun search(query: String, direction: SearchDirection) {
        searchJob?.cancel()
        if (query.isBlank()) {
            searchJob = intent { reduce { state.copy(results = emptyList(), similar = emptyList(), isSearching = false) } }
            return
        }
        searchJob = intent {
            delay(DEBOUNCE)
            searchWordsUseCase(query, direction)
                .onSuccess { results ->
                    val similar = if (results.isEmpty() && direction == SearchDirection.EN_UZ) {
                        getSimilarWordsUseCase(query).getOrDefault(emptyList())
                    } else {
                        emptyList()
                    }
                    reduce { state.copy(results = results, similar = similar, isSearching = false) }
                }
                .onFailure { e ->
                    reduce { state.copy(isSearching = false) }
                    postSideEffect(SearchContract.SideEffect.ShowMessage(UiMessage(e.toMessageRes())))
                }
        }
    }

    private fun openWord(english: String) = intent {
        directions.navigateToDetail(english)
    }

    private fun speakWordOfDay() = intent {
        val word = state.wordOfDay ?: return@intent
        speakUseCase(word.english).onFailure { e ->
            postSideEffect(SearchContract.SideEffect.ShowMessage(UiMessage(e.toMessageRes())))
        }
    }

    private fun removeHistory(english: String) = intent {
        removeHistoryUseCase(english)
    }

    private fun clearHistory() = intent {
        clearHistoryUseCase()
    }

    private fun loadWordOfDay() = intent {
        getWordOfDayUseCase().onSuccess { word ->
            reduce { state.copy(wordOfDay = word) }
        }
    }

    private fun observeHistory() = intent {
        getHistoryUseCase().collect { result ->
            result.onSuccess { history ->
                reduce { state.copy(history = history) }
            }
        }
    }

    private fun String.firstLetter(): String {
        val char = firstOrNull()?.uppercaseChar() ?: return "#"
        return if (char.isLetter()) char.toString() else "#"
    }

    private companion object {
        const val DEBOUNCE = 250L
    }
}
