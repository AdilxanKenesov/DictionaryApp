package uz.gita.dictionaryapp.presenter.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.gita.dictionaryapp.domain.model.QuizMode

// "EN → UZ": strelka belgi emas, haqiqiy ikonka
@Composable
fun DirectionLabel(
    mode: QuizMode,
    modifier: Modifier = Modifier
) {
    val (from, to) = when (mode) {
        QuizMode.EN_UZ -> "EN" to "UZ"
        QuizMode.UZ_EN -> "UZ" to "EN"
    }
    val style = LocalTextStyle.current
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(text = from, style = style)
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .size(16.dp)
        )
        Text(text = to, style = style)
    }
}
