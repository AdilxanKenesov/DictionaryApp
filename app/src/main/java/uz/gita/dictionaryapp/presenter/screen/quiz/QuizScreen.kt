package uz.gita.dictionaryapp.presenter.screen.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import uz.gita.dictionaryapp.R
import uz.gita.dictionaryapp.domain.model.PracticeSource
import uz.gita.dictionaryapp.domain.model.QuizMode
import uz.gita.dictionaryapp.domain.model.QuizQuestion
import uz.gita.dictionaryapp.presenter.ui.components.DirectionLabel
import uz.gita.dictionaryapp.presenter.ui.components.PosChip
import uz.gita.dictionaryapp.presenter.ui.components.SpeakButton
import uz.gita.dictionaryapp.presenter.ui.components.AppSnackbarHost
import uz.gita.dictionaryapp.presenter.ui.components.rememberMessenger
import uz.gita.dictionaryapp.presenter.ui.theme.DictionaryAppTheme
import uz.gita.dictionaryapp.presenter.ui.theme.LocalExtraColors

data class QuizScreen(
    val source: PracticeSource,
    val mode: QuizMode
) : Screen {

    @Composable
    override fun Content() {
        val viewModel: QuizContract.ViewModel = getViewModel<QuizViewModel>()
        val uiState by viewModel.container.stateFlow.collectAsState()
        val messenger = rememberMessenger()

        LaunchedEffect(source, mode) {
            viewModel.onEventDispatcher(QuizContract.Intent.Load(source, mode))
        }
        LaunchedEffect(Unit) {
            viewModel.container.sideEffectFlow.collect { effect ->
                when (effect) {
                    is QuizContract.SideEffect.ShowMessage -> messenger.show(effect.message)
                }
            }
        }
        QuizContent(uiState = uiState, onEventDispatcher = viewModel::onEventDispatcher)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun QuizContent(
        uiState: QuizContract.UiState,
        onEventDispatcher: (QuizContract.Intent) -> Unit
    ) {
        Scaffold(
            snackbarHost = { AppSnackbarHost() },
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.quiz_title))
                            Text(" · ")
                            DirectionLabel(mode = uiState.mode)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { onEventDispatcher(QuizContract.Intent.Close) }) {
                            Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.cd_close))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            }
        ) { innerPadding ->
            val question = uiState.current
            if (uiState.isLoading || question == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Scaffold
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(top = 4.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.question_number, uiState.index + 1, uiState.questions.size),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = stringResource(R.string.correct),
                        tint = LocalExtraColors.current.success,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(18.dp)
                    )
                    Text(
                        text = uiState.score.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = LocalExtraColors.current.success
                    )
                }
                val progress by animateFloatAsState(
                    (uiState.index + 1).toFloat() / uiState.questions.size,
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
                AnimatedContent(
                    targetState = uiState.index,
                    transitionSpec = {
                        (slideInHorizontally { it / 3 } + fadeIn()) togetherWith (slideOutHorizontally { -it / 3 } + fadeOut())
                    },
                    label = "question"
                ) { index ->
                    val current = uiState.questions[index]
                    Column {
                        QuestionCard(
                            question = current,
                            canSpeak = uiState.mode == QuizMode.EN_UZ,
                            onSpeak = { onEventDispatcher(QuizContract.Intent.Speak) }
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(top = 20.dp, bottom = 24.dp)
                        ) {
                            current.options.forEachIndexed { i, option ->
                                AnswerOption(
                                    letter = "ABCD"[i % 4].toString(),
                                    text = option,
                                    state = when {
                                        uiState.selected == null || index != uiState.index -> AnswerState.IDLE
                                        option == current.answer -> AnswerState.CORRECT
                                        option == uiState.selected -> AnswerState.WRONG
                                        else -> AnswerState.IDLE
                                    },
                                    onClick = { onEventDispatcher(QuizContract.Intent.Select(option)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun QuestionCard(
        question: QuizQuestion,
        canSpeak: Boolean,
        onSpeak: () -> Unit
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 26.dp)
            ) {
                Text(
                    text = stringResource(if (canSpeak) R.string.choose_translation else R.string.choose_word),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = question.prompt,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (canSpeak) SpeakButton(onClick = onSpeak, modifier = Modifier.padding(start = 8.dp))
                }
                PosChip(type = question.type)
            }
        }
    }

    private enum class AnswerState { IDLE, CORRECT, WRONG }

    @Composable
    private fun AnswerOption(
        letter: String,
        text: String,
        state: AnswerState,
        onClick: () -> Unit
    ) {
        val colors = MaterialTheme.colorScheme
        val extra = LocalExtraColors.current
        val haptic = LocalHapticFeedback.current
        val accent = when (state) {
            AnswerState.IDLE -> colors.outline
            AnswerState.CORRECT -> extra.success
            AnswerState.WRONG -> colors.error
        }
        val container by animateColorAsState(
            when (state) {
                AnswerState.IDLE -> colors.surface
                AnswerState.CORRECT -> extra.successContainer
                AnswerState.WRONG -> colors.errorContainer
            },
            label = "container"
        )
        val content = when (state) {
            AnswerState.IDLE -> colors.onSurface
            AnswerState.CORRECT -> extra.success
            AnswerState.WRONG -> colors.error
        }
        Surface(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            shape = RoundedCornerShape(16.dp),
            color = container,
            contentColor = content,
            border = BorderStroke(1.5.dp, accent)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(
                            if (state == AnswerState.IDLE) colors.surfaceContainer else accent,
                            RoundedCornerShape(9.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = letter,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (state == AnswerState.IDLE) colors.onSurfaceVariant else colors.surface
                    )
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuizContentPreview() {
    DictionaryAppTheme {
        QuizScreen(PracticeSource.RANDOM, QuizMode.EN_UZ).QuizContent(
            uiState = QuizContract.UiState(
                isLoading = false,
                questions = listOf(QuizQuestion("brave", "brave", "adj", listOf("chiroyli", "mard", "rostgo'y", "aqlli"), "mard")),
                score = 3,
                selected = "chiroyli"
            ),
            onEventDispatcher = {}
        )
    }
}
