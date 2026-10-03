package uz.gita.dictionaryapp.presenter.ui.components

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import uz.gita.dictionaryapp.R

fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(getString(R.string.app_name), text))
}

// false qaytsa — buni ochadigan ilova yo'q
fun Context.shareText(text: String): Boolean {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    return startSafely(Intent.createChooser(intent, null))
}

// TTS ovoz paketlarini o'rnatish oynasi
fun Context.installTtsData(): Boolean =
    startSafely(Intent("android.speech.tts.engine.INSTALL_TTS_DATA"))

private fun Context.startSafely(intent: Intent): Boolean =
    try {
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        true
    } catch (_: ActivityNotFoundException) {
        false
    }
