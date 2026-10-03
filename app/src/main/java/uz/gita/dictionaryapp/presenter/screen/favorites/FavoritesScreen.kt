package uz.gita.dictionaryapp.presenter.screen.favorites

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import uz.gita.dictionaryapp.R
import uz.gita.dictionaryapp.domain.model.FavoriteSort
import uz.gita.dictionaryapp.domain.model.FavoriteWord
import uz.gita.dictionaryapp.domain.model.WordItem
import uz.gita.dictionaryapp.presenter.screen.search.SearchScreen
import uz.gita.dictionaryapp.presenter.ui.components.EmptyState
import uz.gita.dictionaryapp.presenter.ui.components.SearchField
import uz.gita.dictionaryapp.presenter.ui.components.WordListItem
import uz.gita.dictionaryapp.presenter.ui.components.rememberTabOptions
import uz.gita.dictionaryapp.presenter.ui.components.rememberMessenger
import uz.gita.dictionaryapp.presenter.ui.theme.Dimens
import uz.gita.dictionaryapp.presenter.ui.theme.DictionaryAppTheme
import uz.gita.dictionaryapp.presenter.ui.theme.FavoriteRed

object FavoritesScreen : Tab {

    private fun readResolve(): Any = FavoritesScreen

    override val options: TabOptions
        @Composable
        get() = rememberTabOptions(
            index = 1,
            title = R.string.tab_favorites,
            icon = Icons.Rounded.FavoriteBorder,
            selectedIcon = Icons.Rounded.Favorite
        )

    @Composable
    override fun Content() {
        val viewModel: FavoritesContract.ViewModel = getViewModel<FavoritesViewModel>()
        val uiState by viewModel.container.stateFlow.collectAsState()
        val messenger = rememberMessenger()
        val tabNavigator = LocalTabNavigator.current
        val undoLabel = stringResource(R.string.undo)
        val removedText = stringResource(R.string.favorite_removed)

        LaunchedEffect(Unit) {
            viewModel.container.sideEffectFlow.collect { effect ->
                when (effect) {
                    is FavoritesContract.SideEffect.ShowMessage -> messenger.show(effect.message)
                    FavoritesContract.SideEffect.OpenSearchTab -> tabNavigator.current = SearchScreen
                    is FavoritesContract.SideEffect.ShowUndo ->
                        messenger.show(removedText.format(effect.english), actionLabel = undoLabel) {
                            viewModel.onEventDispatcher(FavoritesContract.Intent.Restore(effect.english, effect.addedAt))
                        }
                }
            }
        }
        FavoritesContent(
            uiState = uiState,
            onEventDispatcher = viewModel::onEventDispatcher
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FavoritesContent(
        uiState: FavoritesContract.UiState,
        onEventDispatcher: (FavoritesContract.Intent) -> Unit
    ) {
        Scaffold(contentWindowInsets = WindowInsets.statusBars) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Dimens.ScreenPadding)
            ) {
                Row(
                    modifier = Modifier.padding(top = 10.dp, bottom = 14.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(text = stringResource(R.string.tab_favorites), style = MaterialTheme.typography.headlineSmall)
                    if (uiState.totalCount > 0) {
                        Text(
                            text = " · ${uiState.totalCount}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }

                when {
                    uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }

                    uiState.totalCount == 0 -> EmptyState(
                        icon = Icons.Rounded.FavoriteBorder,
                        iconTint = FavoriteRed,
                        title = stringResource(R.string.favorites_empty_title),
                        message = stringResource(R.string.favorites_empty_message),
                        messageIcon = Icons.Rounded.FavoriteBorder,
                        modifier = Modifier.padding(top = 60.dp)
                    ) {
                        Button(onClick = { onEventDispatcher(FavoritesContract.Intent.OpenSearch) }) {
                            Icon(Icons.Rounded.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.favorites_search_button))
                        }
                    }

                    else -> {
                        SearchField(
                            query = uiState.query,
                            onQueryChange = { onEventDispatcher(FavoritesContract.Intent.QueryChanged(it)) },
                            hint = stringResource(R.string.favorites_search_hint),
                            height = 46.dp
                        )
                        SortSelector(
                            sort = uiState.sort,
                            onSortChange = { onEventDispatcher(FavoritesContract.Intent.SortChanged(it)) }
                        )
                        if (uiState.favorites.isEmpty()) {
                            EmptyState(
                                icon = Icons.Rounded.SearchOff,
                                title = stringResource(R.string.favorites_no_match),
                                message = uiState.query
                            )
                        } else {
                            FavoriteList(uiState.favorites, onEventDispatcher)
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun SortSelector(
        sort: FavoriteSort,
        onSortChange: (FavoriteSort) -> Unit
    ) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.padding(vertical = 12.dp)) {
            FavoriteSort.entries.forEachIndexed { index, item ->
                SegmentedButton(
                    selected = sort == item,
                    onClick = { onSortChange(item) },
                    shape = SegmentedButtonDefaults.itemShape(index, FavoriteSort.entries.size),
                    icon = {}
                ) {
                    Text(
                        stringResource(
                            when (item) {
                                FavoriteSort.NEWEST -> R.string.sort_newest
                                FavoriteSort.ALPHABET -> R.string.sort_alphabet
                            }
                        )
                    )
                }
            }
        }
    }

    @Composable
    private fun FavoriteList(
        favorites: List<FavoriteWord>,
        onEventDispatcher: (FavoritesContract.Intent) -> Unit
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(favorites, key = { it.word.english }) { favorite ->
                SwipeToDeleteItem(
                    onDelete = { onEventDispatcher(FavoritesContract.Intent.Remove(favorite.word.english)) },
                    modifier = Modifier.animateItem()
                ) {
                    WordListItem(
                        word = favorite.word,
                        onClick = { onEventDispatcher(FavoritesContract.Intent.OpenWord(favorite.word.english)) }
                    )
                }
            }
        }
    }

    @Composable
    private fun SwipeToDeleteItem(
        onDelete: () -> Unit,
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        val state = rememberSwipeToDismissBoxState()
        val shape = RoundedCornerShape(16.dp)
        SwipeToDismissBox(
            state = state,
            modifier = modifier.clip(shape),
            enableDismissFromStartToEnd = false,
            onDismiss = { if (it == SwipeToDismissBoxValue.EndToStart) onDelete() },
            backgroundContent = {
                val active = state.targetValue == SwipeToDismissBoxValue.EndToStart
                val scale by animateFloatAsState(if (active) 1.2f else 0.9f, label = "delete")
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.error)
                        .padding(end = 20.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.remove),
                        tint = Color.White,
                        modifier = Modifier.scale(scale)
                    )
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, shape)
                    .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            ) {
                content()
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun FavoritesContentPreview() {
        DictionaryAppTheme {
            FavoritesContent(
                uiState = FavoritesContract.UiState(
                    isLoading = false,
                    totalCount = 2,
                    favorites = listOf(
                        FavoriteWord(WordItem("courage", "noun", "/kʌr.ɪdʒ/", "jasorat; jasurlik"), 2),
                        FavoriteWord(WordItem("beautiful", "adj", "/bju.tɪ.fəl/", "chiroyli"), 1)
                    )
                ),
                onEventDispatcher = {}
            )
        }
    }
}
