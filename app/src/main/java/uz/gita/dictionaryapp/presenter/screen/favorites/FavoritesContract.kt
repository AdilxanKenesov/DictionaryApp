package uz.gita.dictionaryapp.presenter.screen.favorites

import org.orbitmvi.orbit.OrbitContainerHost
import uz.gita.dictionaryapp.domain.model.FavoriteSort
import uz.gita.dictionaryapp.domain.model.FavoriteWord
import uz.gita.dictionaryapp.presenter.ui.components.UiMessage

interface FavoritesContract {

    interface ViewModel : OrbitContainerHost<UiState, UiState, SideEffect> {
        fun onEventDispatcher(intent: Intent)
    }

    sealed interface Intent {
        data class QueryChanged(val query: String) : Intent
        data class SortChanged(val sort: FavoriteSort) : Intent
        data class OpenWord(val english: String) : Intent
        data class Remove(val english: String) : Intent
        data class Restore(val english: String, val addedAt: Long) : Intent
        data object OpenSearch : Intent
    }

    sealed interface SideEffect {
        data class ShowMessage(val message: UiMessage) : SideEffect
        data class ShowUndo(val english: String, val addedAt: Long) : SideEffect
        data object OpenSearchTab : SideEffect
    }

    data class UiState(
        val isLoading: Boolean = true,
        val favorites: List<FavoriteWord> = emptyList(),
        val totalCount: Int = 0,
        val query: String = "",
        val sort: FavoriteSort = FavoriteSort.NEWEST
    )

    interface Directions {
        fun navigateToDetail(english: String)
    }
}
