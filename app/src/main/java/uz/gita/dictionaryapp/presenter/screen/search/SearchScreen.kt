package uz.gita.dictionaryapp.presenter.screen.search

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import cafe.adriel.voyager.hilt.getViewModel
import kotlinx.coroutines.flow.flowOf
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import uz.gita.dictionaryapp.R
import uz.gita.dictionaryapp.domain.model.SearchDirection
import uz.gita.dictionaryapp.domain.model.WordItem
import uz.gita.dictionaryapp.presenter.ui.components.EmptyState
import uz.gita.dictionaryapp.presenter.ui.components.SearchField
import uz.gita.dictionaryapp.presenter.ui.components.SectionHeader
import uz.gita.dictionaryapp.presenter.ui.components.WordListItem
import uz.gita.dictionaryapp.presenter.ui.components.rememberTabOptions
import uz.gita.dictionaryapp.presenter.ui.components.rememberMessenger
import uz.gita.dictionaryapp.presenter.ui.theme.Dimens
import uz.gita.dictionaryapp.presenter.ui.theme.DictionaryAppTheme

object SearchScreen : Tab {

    private fun readResolve(): Any = SearchScreen

    override val options: TabOptions
        @Composable
        get() = rememberTabOptions(
            index = 0,
            title = R.string.tab_dictionary,
            icon = Icons.Outlined.AutoStories,
            selectedIcon = Icons.Rounded.AutoStories
        )

    @Composable
    override fun Content() {
        val viewModel: SearchContract.ViewModel = getViewModel<SearchViewModel>()
        val uiState by viewModel.container.stateFlow.collectAsState()
        val words = viewModel.words.collectAsLazyPagingItems()
        val messenger = rememberMessenger()

        LaunchedEffect(Unit) {
            viewModel.container.sideEffectFlow.collect { effect ->
                when (effect) {
                    is SearchContract.SideEffect.ShowMessage -> messenger.show(effect.message)
                }
            }
        }
        SearchContent(uiState = uiState, words = words, onEventDispatcher = viewModel::onEventDispatcher)
    }

    @Composable
    fun SearchContent(
        uiState: SearchContract.UiState,
        words: LazyPagingItems<SearchContract.ListItem>,
        onEventDispatcher: (SearchContract.Intent) -> Unit
    ) {
        Scaffold(contentWindowInsets = WindowInsets.statusBars) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Dimens.ScreenPadding)
            ) {
                Text(
                    text = stringResource(R.string.tab_dictionary),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 10.dp, bottom = 14.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceS)
                ) {
                    SearchField(
                        query = uiState.query,
                        onQueryChange = { onEventDispatcher(SearchContract.Intent.QueryChanged(it)) },
                        hint = stringResource(
                            if (uiState.direction == SearchDirection.EN_UZ) R.string.search_hint_en
                            else R.string.search_hint_uz
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    DirectionButton(
                        direction = uiState.direction,
                        onClick = { onEventDispatcher(SearchContract.Intent.ToggleDirection) }
                    )
                }
                Box(modifier = Modifier.height(3.dp).fillMaxWidth()) {
                    if (uiState.isSearching) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .offset(y = 2.dp)
                        )
                    }
                }

                when {
                    uiState.query.isBlank() -> HomeState(uiState, words, onEventDispatcher)
                    uiState.results.isEmpty() && !uiState.isSearching -> NotFoundState(uiState, onEventDispatcher)
                    else -> ResultList(uiState, onEventDispatcher)
                }
            }
        }
    }

    @Composable
    private fun DirectionButton(
        direction: SearchDirection,
        onClick: () -> Unit
    ) {
        val isEnglish = direction == SearchDirection.EN_UZ
        val container by animateColorAsState(
            if (isEnglish) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
            label = "direction"
        )
        val content = if (isEnglish) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onSecondaryContainer
        val label = stringResource(R.string.cd_swap_direction)

        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(18.dp),
            color = container,
            contentColor = content,
            modifier = Modifier.size(width = 64.dp, height = Dimens.SearchHeight)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isEnglish) "EN" else "UZ",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Icon(Icons.Rounded.SwapHoriz, contentDescription = label, modifier = Modifier.size(18.dp))
            }
        }
    }

    @Composable
    private fun HomeState(
        uiState: SearchContract.UiState,
        words: LazyPagingItems<SearchContract.ListItem>,
        onEventDispatcher: (SearchContract.Intent) -> Unit
    ) {
        val focusManager = LocalFocusManager.current
        val openWord: (String) -> Unit = { english ->
            focusManager.clearFocus()
            onEventDispatcher(SearchContract.Intent.OpenWord(english))
        }
        LazyColumn(contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp)) {
            uiState.wordOfDay?.let { word ->
                item(key = "wod") {
                    WordOfDayCard(
                        word = word,
                        onClick = { openWord(word.english) },
                        onSpeak = { onEventDispatcher(SearchContract.Intent.SpeakWordOfDay) }
                    )
                }
            }
            if (uiState.history.isNotEmpty()) {
                item(key = "history") {
                    Column(modifier = Modifier.animateItem()) {
                        SectionHeader(
                            title = stringResource(R.string.recent_searches),
                            actionText = stringResource(R.string.clear),
                            onAction = { onEventDispatcher(SearchContract.Intent.ClearHistory) },
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        HistoryChips(
                            history = uiState.history,
                            onClick = openWord,
                            onRemove = { onEventDispatcher(SearchContract.Intent.RemoveHistory(it)) }
                        )
                    }
                }
            }
            item(key = "all_words_header") {
                Text(
                    text = stringResource(R.string.all_words),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 20.dp, bottom = 4.dp)
                )
            }
            items(
                count = words.itemCount,
                key = words.itemKey { entry ->
                    when (entry) {
                        is SearchContract.ListItem.Header -> entry.key
                        is SearchContract.ListItem.Word -> "word_${entry.word.english}"
                    }
                },
                contentType = words.itemContentType { it::class }
            ) { index ->
                when (val entry = words[index]) {
                    is SearchContract.ListItem.Header -> LetterHeader(entry.letter)
                    is SearchContract.ListItem.Word -> {
                        if (words.peek(index - 1) is SearchContract.ListItem.Word) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                        }
                        WordListItem(word = entry.word, onClick = { openWord(entry.word.english) })
                    }
                    null -> Unit
                }
            }
            if (words.loadState.append is LoadState.Loading || words.loadState.refresh is LoadState.Loading) {
                item(key = "loading") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp))
                    }
                }
            }
        }
    }

    @Composable
    private fun HistoryChips(
        history: List<WordItem>,
        onClick: (String) -> Unit,
        onRemove: (String) -> Unit
    ) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(history, key = { it.english }) { word ->
                Row(
                    modifier = Modifier
                        .animateItem()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .clickable { onClick(word.english) }
                        .padding(start = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = word.english,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                    IconButton(onClick = { onRemove(word.english) }, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(R.string.cd_remove),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun LetterHeader(letter: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(start = 12.dp),
                color = MaterialTheme.colorScheme.outline
            )
        }
    }

    @Composable
    private fun WordOfDayCard(
        word: WordItem,
        onClick: () -> Unit,
        onSpeak: () -> Unit
    ) {
        val colors = MaterialTheme.colorScheme
        val brush = Brush.linearGradient(
            listOf(colors.primary, colors.primary.copy(alpha = 0.85f), colors.secondary)
        )
        val content = colors.onPrimary
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Dimens.LargeRadius))
                .background(brush)
                .clickable(onClick = onClick)
        ) {
            // Bezak uchun yarim shaffof doira
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 40.dp, y = 60.dp)
                    .size(160.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            )
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Star, contentDescription = null, tint = content, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.word_of_day).uppercase(),
                        color = content.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = word.english,
                            style = MaterialTheme.typography.headlineMedium,
                            color = content,
                            modifier = Modifier.padding(top = 8.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = listOf(word.transcript, word.type).filter { it.isNotBlank() }.joinToString(" · "),
                            color = content.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = word.uzbek,
                            color = content,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                    IconButton(
                        onClick = onSpeak,
                        modifier = Modifier
                            .size(52.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                            contentDescription = stringResource(R.string.cd_speak),
                            tint = content
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ResultList(
        uiState: SearchContract.UiState,
        onEventDispatcher: (SearchContract.Intent) -> Unit
    ) {
        val focusManager = LocalFocusManager.current
        val uzbekFirst = uiState.direction == SearchDirection.UZ_EN
        Column {
            if (uiState.results.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.results_count, uiState.results.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 8.dp)
                )
            }
            LazyColumn(
                modifier = Modifier
                    .clip(RoundedCornerShape(Dimens.CardRadius))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(Dimens.CardRadius)),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                itemsIndexed(
                    uiState.results,
                    key = { index, word -> "$index-${word.english}-${word.uzbek}" }
                ) { index, word ->
                    if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    WordListItem(
                        word = word,
                        highlight = uiState.query,
                        uzbekFirst = uzbekFirst,
                        onClick = {
                            focusManager.clearFocus()
                            onEventDispatcher(SearchContract.Intent.OpenWord(word.english))
                        }
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun NotFoundState(
        uiState: SearchContract.UiState,
        onEventDispatcher: (SearchContract.Intent) -> Unit
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            EmptyState(
                icon = Icons.Rounded.SearchOff,
                title = stringResource(R.string.not_found_title, uiState.query.trim()),
                message = stringResource(
                    if (uiState.similar.isEmpty()) R.string.not_found_message_short else R.string.not_found_message
                )
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.similar.forEach { word ->
                    Text(
                        text = word,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                            .clickable { onEventDispatcher(SearchContract.Intent.SelectSuggestion(word)) }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun SearchContentPreview() {
        DictionaryAppTheme {
            SearchContent(
                uiState = SearchContract.UiState(
                    wordOfDay = WordItem("courage", "noun", "/kʌr.ɪdʒ/", "jasorat; jasurlik"),
                    history = listOf(
                        WordItem("light", "noun", "/laɪt/", "yorug'lik; yoritmoq"),
                        WordItem("book", "noun", "/bʊk/", "kitob")
                    )
                ),
                words = flowOf(
                    PagingData.from(
                        listOf<SearchContract.ListItem>(
                            SearchContract.ListItem.Header("A", "h_a"),
                            SearchContract.ListItem.Word(WordItem("abandon", "verb", "/əbæn.dən/", "tark qilmoq")),
                            SearchContract.ListItem.Word(WordItem("achieve", "verb", "/ətʃiv/", "erishmoq"))
                        )
                    )
                ).collectAsLazyPagingItems(),
                onEventDispatcher = {}
            )
        }
    }
}
