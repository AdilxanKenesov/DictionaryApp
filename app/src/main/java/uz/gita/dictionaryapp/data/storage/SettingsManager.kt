package uz.gita.dictionaryapp.data.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import uz.gita.dictionaryapp.domain.model.Accent
import uz.gita.dictionaryapp.domain.model.AppLanguage
import uz.gita.dictionaryapp.domain.model.ThemeMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(
    private val pref: SharedPreferences
) {

    var themeMode: ThemeMode
        get() {
            val name = pref.getString(KEY_THEME, ThemeMode.DARK.name)
            return ThemeMode.entries.find { it.name == name } ?: ThemeMode.DARK
        }
        set(value) {
            pref.edit { putString(KEY_THEME, value.name) }
        }

    var language: AppLanguage
        get() = readLanguage(pref)
        set(value) {
            // commit: Activity darhol qayta yaratiladi va yangi tilni o'qishi kerak
            pref.edit(commit = true) { putString(KEY_LANGUAGE, value.name) }
        }

    var accent: Accent
        get() {
            val name = pref.getString(KEY_ACCENT, Accent.US.name)
            return Accent.entries.find { it.name == name } ?: Accent.US
        }
        set(value) {
            pref.edit { putString(KEY_ACCENT, value.name) }
        }

    var speechRate: Float
        get() = pref.getFloat(KEY_SPEECH_RATE, 1f)
        set(value) {
            pref.edit { putFloat(KEY_SPEECH_RATE, value) }
        }

    companion object {
        const val PREF_NAME = "dictionary_pref"
        private const val KEY_LANGUAGE = "language"

        // Activity.attachBaseContext da Hilt hali ishlamaydi, shuning uchun to'g'ridan-to'g'ri o'qiladi
        fun readLanguage(pref: SharedPreferences): AppLanguage {
            val name = pref.getString(KEY_LANGUAGE, AppLanguage.EN.name)
            return AppLanguage.entries.find { it.name == name } ?: AppLanguage.EN
        }

        private const val KEY_THEME = "theme_mode"
        private const val KEY_ACCENT = "accent"
        private const val KEY_SPEECH_RATE = "speech_rate"
    }
}
