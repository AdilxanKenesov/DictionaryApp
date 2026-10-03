package uz.gita.dictionaryapp.utils

import androidx.annotation.StringRes
import uz.gita.dictionaryapp.R
import uz.gita.dictionaryapp.domain.model.SpeechUnavailableException

@StringRes
fun Throwable.toMessageRes(): Int = when (this) {
    is SpeechUnavailableException -> R.string.tts_unavailable
    else -> R.string.error_generic
}
