package uz.gita.dictionaryapp.domain.model

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val language: AppLanguage = AppLanguage.EN,
    val accent: Accent = Accent.US,
    val speechRate: Float = 1f
)
