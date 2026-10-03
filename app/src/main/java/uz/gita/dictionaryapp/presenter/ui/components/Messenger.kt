package uz.gita.dictionaryapp.presenter.ui.components

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Butun ilova uchun bitta Snackbar: ekran yopilsa ham xabar yo'qolmaydi
class Messenger(
    val hostState: SnackbarHostState,
    private val scope: CoroutineScope,
    private val context: Context
) {

    fun show(
        message: UiMessage,
        actionLabel: String? = null,
        onAction: () -> Unit = {}
    ) {
        show(context.getString(message.resId, *message.args.toTypedArray()), actionLabel, onAction)
    }

    fun show(
        text: String,
        actionLabel: String? = null,
        onAction: () -> Unit = {}
    ) {
        scope.launch {
            hostState.currentSnackbarData?.dismiss()
            val result = hostState.showSnackbar(
                message = text,
                actionLabel = actionLabel,
                withDismissAction = actionLabel == null,
                duration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) onAction()
        }
    }
}

val LocalMessenger = staticCompositionLocalOf<Messenger?> { null }

@Composable
fun rememberMessenger(): Messenger = checkNotNull(LocalMessenger.current) { "Messenger is not provided" }

// Har bir Scaffold shu hostni qo'yadi; preview'da Messenger bo'lmasa hech narsa chizilmaydi
@Composable
fun AppSnackbarHost(modifier: Modifier = Modifier) {
    LocalMessenger.current?.let { SnackbarHost(hostState = it.hostState, modifier = modifier) }
}
