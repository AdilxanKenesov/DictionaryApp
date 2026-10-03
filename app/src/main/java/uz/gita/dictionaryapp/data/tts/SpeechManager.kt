package uz.gita.dictionaryapp.data.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import uz.gita.dictionaryapp.domain.model.SpeechUnavailableException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val ready = CompletableDeferred<Boolean>()

    // TTS dvigateli asinxron ishga tushadi, tayyor bo'lishini ready orqali kutamiz
    private val tts = TextToSpeech(context) { status ->
        ready.complete(status == TextToSpeech.SUCCESS)
    }

    suspend fun speak(text: String, locale: Locale, rate: Float) {
        val isReady = withTimeoutOrNull(INIT_TIMEOUT) { ready.await() } ?: false
        if (!isReady) throw SpeechUnavailableException()

        val result = tts.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            throw SpeechUnavailableException()
        }
        tts.setSpeechRate(rate)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID)
    }

    private companion object {
        const val INIT_TIMEOUT = 3_000L
        const val UTTERANCE_ID = "word"
    }
}
