package uz.gita.dictionaryapp.presenter.screen.flashcard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import kotlinx.coroutines.launch
import uz.gita.dictionaryapp.R
import uz.gita.dictionaryapp.domain.model.PracticeSource
import uz.gita.dictionaryapp.domain.model.WordItem
import uz.gita.dictionaryapp.presenter.ui.components.EmptyState
import uz.gita.dictionaryapp.presenter.ui.components.PosChip
import uz.gita.dictionaryapp.presenter.ui.components.SpeakButton
import uz.gita.dictionaryapp.presenter.ui.components.AppSnackbarHost
import uz.gita.dictionaryapp.presenter.ui.components.rememberMessenger
import uz.gita.dictionaryapp.presenter.ui.theme.DictionaryAppTheme
import uz.gita.dictionaryapp.presenter.ui.theme.LocalExtraColors
import kotlin.math.abs

data class FlashcardScreen(val source: PracticeSource) : Screen {

    @Composable
    override fun Content() {
        val viewModel: FlashcardContract.ViewModel = getViewModel<FlashcardViewModel>()
        val uiState by viewModel.container.stateFlow.collectAsState()
        val messenger = rememberMessenger()

        LaunchedEffect(source) {
            viewModel.onEventDispatcher(FlashcardContract.Intent.Load(source))
        }
        LaunchedEffect(Unit) {
            viewModel.container.sideEffectFlow.collect { effect ->
                when (effect) {
                    is FlashcardContract.SideEffect.ShowMessage -> messenger.show(effect.message)
                }
            }
        }
        FlashcardContent(uiState = uiState, onEventDispatcher = viewModel::onEventDispatcher)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FlashcardContent(
        uiState: FlashcardContract.UiState,
        onEventDispatcher: (FlashcardContract.Intent) -> Unit
    ) {
        Scaffold(
            snackbarHost = { AppSnackbarHost() },
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.flashcard_title)) },
                    navigationIcon = {
                        IconButton(onClick = { onEventDispatcher(FlashcardContract.Intent.Close) }) {
                            Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.cd_close))
                        }
                    },
                    actions = {
                        if (uiState.total > 0) {
                            Text(
                                text = stringResource(R.string.progress, uiState.known, uiState.total),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
            ) {
                val progress by animateFloatAsState(
                    if (uiState.total == 0) 0f else uiState.known.toFloat() / uiState.total,
                    label = "progress"
                )
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    gapSize = 0.dp,
                    drawStopIndicator = {}
                )
                val current = uiState.current
                when {
                    uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }

                    uiState.isFinished -> FinishedState(uiState.known, onEventDispatcher)

                    current != null -> {
                        SwipeCard(
                            word = current,
                            isFlipped = uiState.isFlipped,
                            onFlip = { onEventDispatcher(FlashcardContract.Intent.Flip) },
                            onSpeak = { onEventDispatcher(FlashcardContract.Intent.Speak) },
                            onKnow = { onEventDispatcher(FlashcardContract.Intent.Know) },
                            onRepeat = { onEventDispatcher(FlashcardContract.Intent.Repeat) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 24.dp)
                        )
                        Actions(
                            onKnow = { onEventDispatcher(FlashcardContract.Intent.Know) },
                            onRepeat = { onEventDispatcher(FlashcardContract.Intent.Repeat) }
                        )
                    }
                }
            }
        }
    }

    // Kartani o'ngga surish — "Bilaman", chapga — "Takrorlash"
    @Composable
    private fun SwipeCard(
        word: WordItem,
        isFlipped: Boolean,
        onFlip: () -> Unit,
        onSpeak: () -> Unit,
        onKnow: () -> Unit,
        onRepeat: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val offsetX = remember(word) { Animatable(0f) }
        val scope = rememberCoroutineScope()
        val threshold = with(LocalDensity.current) { 110.dp.toPx() }
        val rotation by animateFloatAsState(
            targetValue = if (isFlipped) 180f else 0f,
            animationSpec = tween(450),
            label = "flip"
        )

        Box(
            modifier = modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationX = offsetX.value
                    rotationZ = offsetX.value / 30f
                }
                .pointerInput(word) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                val value = offsetX.value
                                if (abs(value) > threshold) {
                                    offsetX.animateTo(if (value > 0) size.width * 1.5f else -size.width * 1.5f, tween(200))
                                    if (value > 0) onKnow() else onRepeat()
                                    // Navbatda bitta so'z qolsa karta o'sha so'z bilan qaytadi
                                    offsetX.snapTo(0f)
                                } else {
                                    offsetX.animateTo(0f)
                                }
                            }
                        },
                        onHorizontalDrag = { change, amount ->
                            change.consume()
                            scope.launch { offsetX.snapTo(offsetX.value + amount) }
                        }
                    )
                }
        ) {
            Surface(
                onClick = onFlip,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f * density
                    },
                shape = RoundedCornerShape(28.dp),
                color = if (rotation <= 90f) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary,
                border = if (rotation <= 90f) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
                shadowElevation = 6.dp
            ) {
                if (rotation <= 90f) {
                    CardFace(hint = stringResource(R.string.tap_to_flip)) {
                        PosChip(type = word.type, modifier = Modifier.padding(bottom = 16.dp))
                        Text(
                            text = word.english,
                            style = MaterialTheme.typography.headlineLarge,
                            textAlign = TextAlign.Center
                        )
                        if (word.transcript.isNotBlank()) {
                            Text(
                                text = word.transcript,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                        SpeakButton(onClick = onSpeak, modifier = Modifier.padding(top = 18.dp).size(48.dp))
                    }
                } else {
                    // Orqa tomon ko'zgu kabi teskari chiqmasligi uchun yana 180° buramiz
                    Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                        CardFace(hint = stringResource(R.string.tap_again)) {
                            Text(
                                text = word.uzbek.replace("; ", "\n"),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun CardFace(
        hint: String,
        content: @Composable () -> Unit
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
            Text(
                text = hint,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .graphicsLayer { alpha = 0.8f }
            )
        }
    }

    @Composable
    private fun Actions(
        onKnow: () -> Unit,
        onRepeat: () -> Unit
    ) {
        val extra = LocalExtraColors.current
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Button(
                onClick = onRepeat,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.repeat))
            }
            Button(
                onClick = onKnow,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = extra.successContainer,
                    contentColor = extra.success
                )
            ) {
                Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.know))
            }
        }
    }

    @Composable
    private fun FinishedState(
        known: Int,
        onEventDispatcher: (FlashcardContract.Intent) -> Unit
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(
                icon = Icons.Rounded.EmojiEvents,
                iconTint = MaterialTheme.colorScheme.tertiary,
                title = stringResource(R.string.flashcard_done_title),
                message = stringResource(R.string.flashcard_done_message, known)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = { onEventDispatcher(FlashcardContract.Intent.Close) }) {
                        Text(stringResource(R.string.cd_close))
                    }
                    Button(onClick = { onEventDispatcher(FlashcardContract.Intent.Restart) }) {
                        Text(stringResource(R.string.restart))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FlashcardContentPreview() {
    DictionaryAppTheme {
        FlashcardScreen(PracticeSource.RANDOM).FlashcardContent(
            uiState = FlashcardContract.UiState(
                isLoading = false,
                queue = listOf(WordItem("discover", "verb", "/dɪskʌv.ər/", "kashf etmoq")),
                total = 10,
                known = 3
            ),
            onEventDispatcher = {}
        )
    }
}
