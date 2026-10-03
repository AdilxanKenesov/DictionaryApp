package uz.gita.dictionaryapp.presenter.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.gita.dictionaryapp.domain.model.WordItem

// EN rejimida sarlavha inglizcha, UZ rejimida (uzbekFirst) o'zbekcha bo'ladi
@Composable
fun WordListItem(
    word: WordItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    highlight: String = "",
    uzbekFirst: Boolean = false,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.Bottom) {
                HighlightedText(
                    text = if (uzbekFirst) word.uzbek else word.english,
                    highlight = highlight,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (!uzbekFirst && word.transcript.isNotBlank()) {
                    Text(
                        text = word.transcript,
                        modifier = Modifier.padding(start = 6.dp, bottom = 2.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
            Text(
                text = if (uzbekFirst) word.english else word.uzbek,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        PosChip(type = word.type)
        trailing?.invoke()
    }
}
