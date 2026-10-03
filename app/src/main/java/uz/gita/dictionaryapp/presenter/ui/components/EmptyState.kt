package uz.gita.dictionaryapp.presenter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    messageIcon: ImageVector? = null,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .size(110.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(52.dp),
                tint = iconTint
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        MessageText(message = message, icon = messageIcon, iconTint = iconTint)
        if (action != null) {
            Box(modifier = Modifier.padding(top = 12.dp)) { action() }
        }
    }
}

// Matndagi "{icon}" o'rniga haqiqiy ikonka qo'yiladi
@Composable
private fun MessageText(
    message: String,
    icon: ImageVector?,
    iconTint: Color
) {
    val parts = message.split(ICON_TOKEN, limit = 2)
    val text = buildAnnotatedString {
        append(parts[0])
        if (icon != null && parts.size == 2) {
            appendInlineContent(ICON_ID)
            append(parts[1])
        } else if (parts.size == 2) {
            append(parts[1])
        }
    }
    val inlineContent = if (icon == null) emptyMap() else mapOf(
        ICON_ID to InlineTextContent(Placeholder(18.sp, 18.sp, PlaceholderVerticalAlign.Center)) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint)
        }
    )
    Text(
        text = text,
        inlineContent = inlineContent,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}

private const val ICON_TOKEN = "{icon}"
private const val ICON_ID = "icon"
