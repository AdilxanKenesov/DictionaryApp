package uz.gita.dictionaryapp.domain.model

enum class SearchDirection { EN_UZ, UZ_EN }

enum class FavoriteSort { NEWEST, ALPHABET }

enum class ThemeMode { LIGHT, DARK }

enum class Accent { US, UK }

enum class AppLanguage(val tag: String) { UZ("uz"), EN("en") }

enum class PracticeSource { FAVORITES, RANDOM }

enum class QuizMode { EN_UZ, UZ_EN }
