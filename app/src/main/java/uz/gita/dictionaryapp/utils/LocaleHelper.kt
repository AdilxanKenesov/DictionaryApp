package uz.gita.dictionaryapp.utils

import android.content.Context
import android.content.res.Configuration
import uz.gita.dictionaryapp.data.storage.SettingsManager
import uz.gita.dictionaryapp.domain.model.AppLanguage
import java.util.Locale

// Ilova tili tizim tilidan qat'i nazar sozlamalardagi tilda bo'ladi (barcha API darajalarida ishlaydi)
object LocaleHelper {

    fun savedLanguage(context: Context): AppLanguage =
        SettingsManager.readLanguage(context.getSharedPreferences(SettingsManager.PREF_NAME, Context.MODE_PRIVATE))

    fun wrap(context: Context, language: AppLanguage): Context {
        val locale = Locale.forLanguageTag(language.tag)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
