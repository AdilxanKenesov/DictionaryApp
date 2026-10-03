package uz.gita.dictionaryapp.presenter.screen.practice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import uz.gita.dictionaryapp.R
import uz.gita.dictionaryapp.domain.model.PracticeSource
import uz.gita.dictionaryapp.domain.model.PracticeSummary
import uz.gita.dictionaryapp.domain.model.QuizMode
import uz.gita.dictionaryapp.domain.model.QuizStats
import uz.gita.dictionaryapp.presenter.ui.components.DirectionLabel
import uz.gita.dictionaryapp.presenter.ui.components.rememberTabOptions
import uz.gita.dictionaryapp.presenter.ui.components.rememberMessenger
import uz.gita.dictionaryapp.presenter.ui.theme.Dimens
import uz.gita.dictionaryapp.presenter.ui.theme.DictionaryAppTheme

object PracticeScreen : Tab {

    private fun readResolve(): Any = PracticeScreen

    override val options: TabOptions
        @Composable
        get() = rememberTabOptions(
            index = 2,
            title = R.string.tab_practice,
            icon = Icons.Outlined.School,
            selectedIcon = Icons.Rounded.School
        )

    @Composable
    override fun Content() {
        val viewModel: PracticeContract.ViewModel = getViewModel<PracticeViewModel>()
        val uiState by viewModel.container.stateFlow.collectAsState()
        val messenger = rememberMessenger()

        LaunchedEffect(Unit) {
            viewModel.container.sideEffectFlow.collect { effect ->
                when (effect) {
                    is PracticeContract.SideEffect.ShowMessage -> messenger.show(effect.message)
                }
            }
        }
        PracticeContent(uiState = uiState, onEventDispatcher = viewModel::onEventDispatcher)
    }

    @Composable
    fun PracticeContent(
        uiState: PracticeContract.UiState,
        onEventDispatcher: (PracticeContract.Intent) -> Unit
    ) {
        Scaffold(contentWindowInsets = WindowInsets.statusBars) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimens.ScreenPadding, vertical = 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.tab_practice),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 14.dp)
                )
                Label(stringResource(R.string.practice_source))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    SourceOption(
                        icon = Icons.Rounded.Favorite,
                        text = stringResource(R.string.source_favorites, uiState.summary.favoritesCount),
                        selected = uiState.source == PracticeSource.FAVORITES,
                        onClick = { onEventDispatcher(PracticeContract.Intent.SelectSource(PracticeSource.FAVORITES)) },
                        modifier = Modifier.weight(1f)
                    )
                    SourceOption(
                        icon = Icons.Rounded.Casino,
                        text = stringResource(R.string.source_random),
                        selected = uiState.source == PracticeSource.RANDOM,
                        onClick = { onEventDispatcher(PracticeContract.Intent.SelectSource(PracticeSource.RANDOM)) },
                        modifier = Modifier.weight(1f)
                    )
                }

                PracticeCard(
                    icon = Icons.Rounded.Style,
                    title = stringResource(R.string.flashcard_title),
                    description = stringResource(R.string.flashcard_desc),
                    container = MaterialTheme.colorScheme.primaryContainer,
                    content = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = { onEventDispatcher(PracticeContract.Intent.StartFlashcards) }
                )
                PracticeCard(
                    icon = Icons.Rounded.Quiz,
                    title = stringResource(R.string.quiz_title),
                    description = stringResource(R.string.quiz_desc),
                    container = MaterialTheme.colorScheme.secondaryContainer,
                    content = MaterialTheme.colorScheme.onSecondaryContainer,
                    onClick = { onEventDispatcher(PracticeContract.Intent.StartQuiz) }
                ) {
                    QuizModeSelector(
                        mode = uiState.quizMode,
                        onModeChange = { onEventDispatcher(PracticeContract.Intent.SelectQuizMode(it)) }
                    )
                }

                Label(stringResource(R.string.statistics), Modifier.padding(top = 6.dp))
                StatsRow(uiState.summary.stats)
            }
        }
    }

    @Composable
    private fun Label(text: String, modifier: Modifier = Modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier.padding(start = 2.dp, bottom = 8.dp)
        )
    }

    @Composable
    private fun SourceOption(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        text: String,
        selected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val colors = MaterialTheme.colorScheme
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(14.dp),
            color = if (selected) colors.primaryContainer else colors.surface,
            contentColor = if (selected) colors.onPrimaryContainer else colors.onSurface,
            border = BorderStroke(1.dp, if (selected) colors.primary else colors.outline)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp)
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    }

    @Composable
    private fun PracticeCard(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        title: String,
        description: String,
        container: Color,
        content: Color,
        onClick: () -> Unit,
        extra: (@Composable () -> Unit)? = null
    ) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(Dimens.LargeRadius),
            color = container,
            contentColor = content,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.55f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null)
                    }
                    Box(modifier = Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 14.dp)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = content.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
                )
                extra?.invoke()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun QuizModeSelector(
        mode: QuizMode,
        onModeChange: (QuizMode) -> Unit
    ) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.padding(top = 12.dp)) {
            QuizMode.entries.forEachIndexed { index, item ->
                SegmentedButton(
                    selected = mode == item,
                    onClick = { onModeChange(item) },
                    shape = SegmentedButtonDefaults.itemShape(index, QuizMode.entries.size),
                    icon = {},
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.secondary,
                        activeContentColor = MaterialTheme.colorScheme.onSecondary,
                        inactiveContainerColor = Color.Transparent
                    )
                ) {
                    DirectionLabel(mode = item)
                }
            }
        }
    }

    @Composable
    private fun StatsRow(stats: QuizStats) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCell(value = stats.count.toString(), label = stringResource(R.string.stat_tests), modifier = Modifier.weight(1f))
            StatCell(
                value = if (stats.bestTotal == 0) "—" else stringResource(R.string.result_score, stats.bestScore, stats.bestTotal),
                label = stringResource(R.string.stat_best),
                modifier = Modifier.weight(1f)
            )
            StatCell(
                value = if (stats.count == 0) "—" else stringResource(R.string.result_percent, stats.averagePercent),
                label = stringResource(R.string.stat_average),
                modifier = Modifier.weight(1f)
            )
        }
    }

    @Composable
    private fun StatCell(value: String, label: String, modifier: Modifier = Modifier) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
            ) {
                Text(text = value, style = MaterialTheme.typography.titleLarge)
                Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun PracticeContentPreview() {
        DictionaryAppTheme {
            PracticeContent(
                uiState = PracticeContract.UiState(
                    source = PracticeSource.FAVORITES,
                    summary = PracticeSummary(favoritesCount = 24, stats = QuizStats(12, 9, 10, 78))
                ),
                onEventDispatcher = {}
            )
        }
    }
}
