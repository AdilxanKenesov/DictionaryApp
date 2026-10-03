package uz.gita.dictionaryapp.presenter.screen.search

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.orbitmvi.orbit.OrbitContainerHost
import uz.gita.dictionaryapp.domain.model.SearchDirection
import uz.gita.dictionaryapp.domain.model.WordItem
import uz.gita.dictionaryapp.presenter.ui.components.UiMessage

interface SearchContract {

    interface ViewModel : OrbitContainerHost<UiState, UiState, SideEffect> {
        // PagingData state ichida saqlanmaydi, alohida Flow orqali beriladi
        val words: Flow<PagingData<ListItem>>

        fun onEventDispatcher(intent: Intent)
    }

    // "Barcha so'zlar" ro'yxati: harf sarlavhasi yoki so'z
    sealed interface ListItem {
        data class Header(val letter: String, val key: String) : ListItem
        data class Word(val word: WordItem) : ListItem
    }

    sealed interface Intent {
        data class QueryChanged(val query: String) : Intent
        data object ToggleDirection : Intent
        data class OpenWord(val english: String) : Intent
        data class SelectSuggestion(val word: String) : Intent
        data object SpeakWordOfDay : Intent
        data class RemoveHistory(val english: String) : Intent
        data object ClearHistory : Intent
    }

    sealed interface SideEffect {
        data class ShowMessage(val message: UiMessage) : SideEffect
    }

    data class UiState(
        val query: String = "",
        val direction: SearchDirection = SearchDirection.EN_UZ,
        val results: List<WordItem> = emptyList(),
        val similar: List<String> = emptyList(),
        val isSearching: Boolean = false,
        val wordOfDay: WordItem? = null,
        val history: List<WordItem> = emptyList()
    )

    interface Directions {
        fun navigateToDetail(english: String)
    }
}
