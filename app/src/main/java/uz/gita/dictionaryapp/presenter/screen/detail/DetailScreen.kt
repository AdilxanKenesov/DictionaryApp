package uz.gita.dictionaryapp.presenter.screen.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.getViewModel
import uz.gita.dictionaryapp.R
import uz.gita.dictionaryapp.domain.model.Accent
import uz.gita.dictionaryapp.domain.model.DefinitionGroup
import uz.gita.dictionaryapp.domain.model.Sense
import uz.gita.dictionaryapp.domain.model.WordDetail
import uz.gita.dictionaryapp.presenter.ui.components.CountableBadge
import uz.gita.dictionaryapp.presenter.ui.components.EmptyState
import uz.gita.dictionaryapp.presenter.ui.components.OverlineHeader
import uz.gita.dictionaryapp.presenter.ui.components.PosChip
import uz.gita.dictionaryapp.presenter.ui.components.copyToClipboard
import uz.gita.dictionaryapp.presenter.ui.components.installTtsData
import uz.gita.dictionaryapp.presenter.ui.components.shareText
import uz.gita.dictionaryapp.presenter.ui.components.AppSnackbarHost
import uz.gita.dictionaryapp.presenter.ui.components.UiMessage
import uz.gita.dictionaryapp.presenter.ui.components.rememberMessenger
import uz.gita.dictionaryapp.presenter.ui.theme.DictionaryAppTheme
import uz.gita.dictionaryapp.presenter.ui.theme.FavoriteRed

data class DetailScreen(val english: String) : Screen {

    // Bir xil so'z ikki marta ochilsa ham har biri alohida ekran bo'lsin
    override val key: ScreenKey = "detail_${english}_${System.nanoTime()}"

    @Composable
    override fun Content() {
        val viewModel: DetailContract.ViewModel = getViewModel<DetailViewModel>()
        val uiState by viewModel.container.stateFlow.collectAsState()
        val context = LocalContext.current
        val messenger = rememberMessenger()
        val installLabel = stringResource(R.string.install)

        LaunchedEffect(english) {
            viewModel.onEventDispatcher(DetailContract.Intent.Load(english))
        }
        LaunchedEffect(Unit) {
            viewModel.container.sideEffectFlow.collect { effect ->
                when (effect) {
                    is DetailContract.SideEffect.ShowMessage -> messenger.show(effect.message)
                    is DetailContract.SideEffect.CopyText -> context.copyToClipboard(effect.text)
                    is DetailContract.SideEffect.ShareText -> {
                        if (!context.shareText(effect.text)) messenger.show(UiMessage(R.string.no_app_found))
                    }
                    DetailContract.SideEffect.SpeechUnavailable ->
                        messenger.show(UiMessage(R.string.tts_unavailable), actionLabel = installLabel) {
                            if (!context.installTtsData()) messenger.show(UiMessage(R.string.no_app_found))
                        }
                }
            }
        }
        DetailContent(
            uiState = uiState,
            onEventDispatcher = viewModel::onEventDispatcher
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DetailContent(
        uiState: DetailContract.UiState,
        onEventDispatcher: (DetailContract.Intent) -> Unit
    ) {
        val haptic = LocalHapticFeedback.current
        Scaffold(
            snackbarHost = { AppSnackbarHost() },
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { onEventDispatcher(DetailContract.Intent.Back) }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                        }
                    },
                    actions = {
                        if (uiState.detail != null) {
                            IconButton(onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onEventDispatcher(DetailContract.Intent.ToggleFavorite)
                            }) {
                                AnimatedContent(
                                    targetState = uiState.isFavorite,
                                    transitionSpec = {
                                        scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) togetherWith scaleOut()
                                    },
                                    label = "favorite"
                                ) { isFavorite ->
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                        contentDescription = stringResource(
                                            if (isFavorite) R.string.cd_favorite_remove else R.string.cd_favorite_add
                                        ),
                                        tint = if (isFavorite) FavoriteRed else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            IconButton(onClick = { onEventDispatcher(DetailContract.Intent.Copy) }) {
                                Icon(Icons.Rounded.ContentCopy, contentDescription = stringResource(R.string.cd_copy))
                            }
                            IconButton(onClick = { onEventDispatcher(DetailContract.Intent.Share) }) {
                                Icon(Icons.Rounded.Share, contentDescription = stringResource(R.string.cd_share))
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            val detail = uiState.detail
            when {
                uiState.isLoading -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                detail == null -> EmptyState(
                    icon = Icons.Rounded.SearchOff,
                    title = stringResource(R.string.word_not_found),
                    message = english,
                    modifier = Modifier.padding(innerPadding)
                )

                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = innerPadding.calculateTopPadding())
                        .verticalScroll(rememberScrollState())
                ) {
                    Hero(detail = detail, onSpeak = { onEventDispatcher(DetailContract.Intent.Speak(it)) })
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 400.dp),
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 18.dp)
                                .windowInsetsPadding(WindowInsets.navigationBars)
                        ) {
                            OverlineHeader(title = stringResource(R.string.section_uzbek))
                            detail.senses.forEach { SenseRow(it) }

                            if (detail.definitions.isNotEmpty()) {
                                OverlineHeader(
                                    title = stringResource(R.string.section_definitions),
                                    modifier = Modifier.padding(top = 18.dp)
                                )
                                detail.definitions.forEach { group ->
                                    DefinitionGroupView(
                                        group = group,
                                        expanded = group.type in uiState.expandedTypes,
                                        onToggle = { onEventDispatcher(DetailContract.Intent.ToggleExpand(group.type)) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun Hero(
        detail: WordDetail,
        onSpeak: (Accent) -> Unit
    ) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp)) {
            Text(text = detail.english, style = MaterialTheme.typography.displaySmall)
            if (detail.transcript.isNotBlank()) {
                Text(
                    text = detail.transcript,
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = null, fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Row(
                modifier = Modifier.padding(top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SpeakChip(label = stringResource(R.string.accent_us), onClick = { onSpeak(Accent.US) })
                SpeakChip(label = stringResource(R.string.accent_uk), onClick = { onSpeak(Accent.UK) })
            }
        }
    }

    @Composable
    private fun SpeakChip(
        label: String,
        onClick: () -> Unit
    ) {
        FilledTonalButton(onClick = onClick) {
            Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = stringResource(R.string.cd_speak), modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(label)
        }
    }

    @Composable
    private fun SenseRow(sense: Sense) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Row(
                modifier = Modifier
                    .width(104.dp)
                    .padding(top = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PosChip(type = sense.type)
                sense.countable?.let { CountableBadge(text = it) }
            }
            Text(
                text = sense.uzbek,
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = null, fontWeight = FontWeight.SemiBold),
                modifier = Modifier.weight(1f)
            )
        }
    }

    @Composable
    private fun DefinitionGroupView(
        group: DefinitionGroup,
        expanded: Boolean,
        onToggle: () -> Unit
    ) {
        val visible = if (expanded) group.items else group.items.take(COLLAPSED_COUNT)
        val hidden = group.items.size - COLLAPSED_COUNT

        Column(modifier = Modifier.padding(top = 10.dp)) {
            if (group.type.isNotBlank()) {
                Text(
                    text = group.type,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
            visible.forEachIndexed { index, text ->
                Row(modifier = Modifier.padding(bottom = 6.dp)) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(24.dp)
                    )
                    Text(text = text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                }
            }
            if (hidden > 0) {
                Row(
                    modifier = Modifier
                        .clickable(onClick = onToggle)
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (expanded) stringResource(R.string.show_less) else stringResource(R.string.show_more, hidden),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    private companion object {
        const val COLLAPSED_COUNT = 3
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailContentPreview() {
    DictionaryAppTheme {
        DetailScreen("light").DetailContent(
            uiState = DetailContract.UiState(
                isLoading = false,
                detail = WordDetail(
                    english = "light",
                    transcript = "/laɪt/",
                    senses = listOf(Sense("noun", "yorug'lik", "C/U"), Sense("verb", "yoritmoq", null)),
                    definitions = listOf(
                        DefinitionGroup("noun", listOf("That agent, force, or action in nature...", "That which furnishes light.", "The time during which the light of the sun is visible.", "Life; existence.")),
                        DefinitionGroup("verb", listOf("To set fire to; to cause to burn."))
                    )
                ),
                isFavorite = true
            ),
            onEventDispatcher = {}
        )
    }
}
