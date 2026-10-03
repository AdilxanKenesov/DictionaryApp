package uz.gita.dictionaryapp.presenter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// So'z turkumi: noun ko'k, verb teal, adj amber
@Composable
fun PosChip(
    type: String,
    modifier: Modifier = Modifier
) {
    if (type.isBlank()) return
    val colors = MaterialTheme.colorScheme
    val (background, content) = when {
        type.startsWith("noun") || type.endsWith("noun") -> colors.primaryContainer to colors.onPrimaryContainer
        type.startsWith("verb") -> colors.secondaryContainer to colors.onSecondaryContainer
        type.startsWith("adj") -> colors.tertiaryContainer to colors.onTertiaryContainer
        else -> colors.surfaceContainerHigh to colors.onSurfaceVariant
    }
    Text(
        text = type,
        modifier = modifier
            .background(background, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        color = content,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1
    )
}

@Composable
fun CountableBadge(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
            .background(Color.Transparent)
            .padding(horizontal = 6.dp, vertical = 1.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold
    )
}
