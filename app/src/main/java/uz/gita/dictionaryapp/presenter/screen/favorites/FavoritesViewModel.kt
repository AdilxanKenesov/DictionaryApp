package uz.gita.dictionaryapp.presenter.screen.favorites

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import org.orbitmvi.orbit.blockingIntent
import org.orbitmvi.orbit.viewmodel.orbitContainer
import uz.gita.dictionaryapp.domain.model.FavoriteSort
import uz.gita.dictionaryapp.domain.usecase.GetFavoritesUseCase
import uz.gita.dictionaryapp.domain.usecase.RemoveFavoriteUseCase
import uz.gita.dictionaryapp.domain.usecase.RestoreFavoriteUseCase
import uz.gita.dictionaryapp.presenter.ui.components.UiMessage
import uz.gita.dictionaryapp.utils.toMessageRes
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val restoreFavoriteUseCase: RestoreFavoriteUseCase,
    private val directions: FavoritesContract.Directions
) : ViewModel(), FavoritesContract.ViewModel {

    override val container = orbitContainer<FavoritesContract.UiState, FavoritesContract.SideEffect>(
        FavoritesContract.UiState()
    )

    private var favoritesJob: Job? = null

    init {
        observeFavorites(FavoriteSort.NEWEST, "")
    }

    override fun onEventDispatcher(intent: FavoritesContract.Intent) {
        when (intent) {
            is FavoritesContract.Intent.QueryChanged -> changeQuery(intent.query)
            is FavoritesContract.Intent.SortChanged -> changeSort(intent.sort)
            is FavoritesContract.Intent.OpenWord -> openWord(intent.english)
            is FavoritesContract.Intent.Remove -> remove(intent.english)
            is FavoritesContract.Intent.Restore -> restore(intent.english, intent.addedAt)
            FavoritesContract.Intent.OpenSearch -> openSearch()
        }
    }

    // TextField uchun blockingIntent — kursor sakramasligi uchun
    private fun changeQuery(query: String) = blockingIntent {
        reduce { state.copy(query = query) }
        observeFavorites(state.sort, query)
    }

    private fun changeSort(sort: FavoriteSort) = intent {
        if (sort == state.sort) return@intent
        reduce { state.copy(sort = sort) }
        observeFavorites(sort, state.query)
    }

    private fun openWord(english: String) = intent {
        directions.navigateToDetail(english)
    }

    private fun remove(english: String) = intent {
        removeFavoriteUseCase(english)
            .onSuccess { addedAt ->
                if (addedAt != null) postSideEffect(FavoritesContract.SideEffect.ShowUndo(english, addedAt))
            }
            .onFailure { e ->
                postSideEffect(FavoritesContract.SideEffect.ShowMessage(UiMessage(e.toMessageRes())))
            }
    }

    private fun restore(english: String, addedAt: Long) = intent {
        restoreFavoriteUseCase(english, addedAt)
    }

    private fun openSearch() = intent {
        postSideEffect(FavoritesContract.SideEffect.OpenSearchTab)
    }

    // Umumiy son "bo'sh holat"ni filtrdan keyingi "topilmadi"dan ajratish uchun kerak
    private fun observeFavorites(sort: FavoriteSort, query: String) {
        favoritesJob?.cancel()
        favoritesJob = intent {
            combine(getFavoritesUseCase(sort, query), getFavoritesUseCase(sort, "")) { filtered, all ->
                filtered to all
            }.collect { (filtered, all) ->
                filtered.onSuccess { favorites ->
                    reduce {
                        state.copy(
                            isLoading = false,
                            favorites = favorites,
                            totalCount = all.getOrNull()?.size ?: favorites.size
                        )
                    }
                }.onFailure { e ->
                    reduce { state.copy(isLoading = false) }
                    postSideEffect(FavoritesContract.SideEffect.ShowMessage(UiMessage(e.toMessageRes())))
                }
            }
        }
    }
}
