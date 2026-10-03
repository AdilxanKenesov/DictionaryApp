package uz.gita.dictionaryapp.presenter.screen.result

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import uz.gita.dictionaryapp.R
import uz.gita.dictionaryapp.domain.model.PracticeSource
import uz.gita.dictionaryapp.domain.model.QuizMode
import uz.gita.dictionaryapp.domain.model.WrongAnswer
import uz.gita.dictionaryapp.presenter.ui.theme.DictionaryAppTheme
import uz.gita.dictionaryapp.presenter.ui.theme.LocalExtraColors

data class ResultScreen(
    val score: Int,
    val total: Int,
    val wrongAnswers: ArrayList<WrongAnswer>,
    val source: PracticeSource,
    val mode: QuizMode
) : Screen {

    @Composable
    override fun Content() {
        val viewModel: ResultContract.ViewModel = getViewModel<ResultViewModel>()
        ResultContent(onEventDispatcher = viewModel::onEventDispatcher)
    }

    @Composable
    fun ResultContent(onEventDispatcher: (ResultContract.Intent) -> Unit) {
        val percent = if (total == 0) 0 else score * 100 / total
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ScoreRing(percent = percent, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
                val (titleRes, titleIcon) = when {
                    percent >= 80 -> R.string.result_great to Icons.Rounded.EmojiEvents
                    percent >= 50 -> R.string.result_good to Icons.Rounded.ThumbUp
                    else -> R.string.result_try to Icons.Rounded.FitnessCenter
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = titleIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(28.dp)
                    )
                    Text(
                        text = stringResource(titleRes),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    val extra = LocalExtraColors.current
                    CountCell(score, stringResource(R.string.correct), extra.successContainer, extra.success, Modifier.weight(1f))
                    CountCell(
                        total - score,
                        stringResource(R.string.wrong),
                        MaterialTheme.colorScheme.errorContainer,
                        MaterialTheme.colorScheme.error,
                        Modifier.weight(1f)
                    )
                }

                if (wrongAnswers.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_mistakes),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = stringResource(R.string.wrong_answers),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column {
                            wrongAnswers.forEachIndexed { index, wrong ->
                                if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                                WrongRow(wrong) { onEventDispatcher(ResultContract.Intent.OpenWord(wrong.english)) }
                            }
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(vertical = 24.dp)
                ) {
                    OutlinedButton(
                        onClick = { onEventDispatcher(ResultContract.Intent.Home) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(stringResource(R.string.home))
                    }
                    Button(
                        onClick = { onEventDispatcher(ResultContract.Intent.Retry(source, mode)) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.retry))
                    }
                }
            }
        }
    }

    @Composable
    private fun ScoreRing(percent: Int, modifier: Modifier = Modifier) {
        val progress = remember { Animatable(0f) }
        LaunchedEffect(percent) { progress.animateTo(percent / 100f, tween(900)) }
        val track = MaterialTheme.colorScheme.surfaceContainerHigh
        val color = when {
            percent >= 80 -> LocalExtraColors.current.success
            percent >= 50 -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.error
        }
        Box(modifier = modifier.size(170.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                drawArc(track, startAngle = 0f, sweepAngle = 360f, useCenter = false, style = stroke)
                drawArc(color, startAngle = -90f, sweepAngle = 360f * progress.value, useCenter = false, style = stroke)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(R.string.result_score, score, total), style = MaterialTheme.typography.headlineLarge)
                Text(
                    text = stringResource(R.string.result_percent, percent),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    @Composable
    private fun CountCell(
        count: Int,
        label: String,
        container: androidx.compose.ui.graphics.Color,
        content: androidx.compose.ui.graphics.Color,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier
                .background(container, RoundedCornerShape(14.dp))
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count.toString(), style = MaterialTheme.typography.titleLarge, color = content)
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = content)
        }
    }

    @Composable
    private fun WrongRow(
        wrong: WrongAnswer,
        onClick: () -> Unit
    ) {
        val error = MaterialTheme.colorScheme.error
        Surface(onClick = onClick, color = MaterialTheme.colorScheme.surface) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = wrong.prompt, style = MaterialTheme.typography.bodyLarge)
                    val line = stringResource(R.string.wrong_answer_line, wrong.chosen, wrong.answer)
                    Text(
                        text = buildAnnotatedString {
                            val start = line.indexOf(wrong.chosen)
                            append(line)
                            if (start >= 0) {
                                addStyle(SpanStyle(color = error, textDecoration = TextDecoration.LineThrough), start, start + wrong.chosen.length)
                            }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ResultContentPreview() {
    DictionaryAppTheme {
        ResultScreen(
            score = 8,
            total = 10,
            wrongAnswers = arrayListOf(
                WrongAnswer("abandon", "abandon", "erishmoq", "tark qilmoq"),
                WrongAnswer("honest", "honest", "mard", "rostgo'y")
            ),
            source = PracticeSource.RANDOM,
            mode = QuizMode.EN_UZ
        ).ResultContent(onEventDispatcher = {})
    }
}
