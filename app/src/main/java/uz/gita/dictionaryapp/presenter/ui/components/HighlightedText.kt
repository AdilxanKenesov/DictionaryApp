package uz.gita.dictionaryapp.presenter.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle

// Qidiruvdagi mos qismni qalin va primary rangda ko'rsatadi
@Composable
fun HighlightedText(
    text: String,
    highlight: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge
) {
    val color = MaterialTheme.colorScheme.primary
    val annotated = remember(text, highlight, color) {
        val start = if (highlight.isBlank()) -1 else text.indexOf(highlight.trim(), ignoreCase = true)
        buildAnnotatedString {
            if (start < 0) {
                append(text)
            } else {
                val end = start + highlight.trim().length
                append(text.substring(0, start))
                withStyle(SpanStyle(color = color, fontWeight = FontWeight.ExtraBold)) {
                    append(text.substring(start, end))
                }
                append(text.substring(end))
            }
        }
    }
    Text(
        text = annotated,
        modifier = modifier,
        style = style,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}
