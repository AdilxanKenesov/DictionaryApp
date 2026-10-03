package uz.gita.dictionaryapp.domain.model

import java.io.Serializable

data class QuizQuestion(
    val english: String,
    val prompt: String,
    val type: String,
    val options: List<String>,
    val answer: String
)

// Voyager ekran parametri bo'lib o'tadi, shuning uchun Serializable
data class WrongAnswer(
    val english: String,
    val prompt: String,
    val chosen: String,
    val answer: String
) : Serializable

data class QuizStats(
    val count: Int = 0,
    val bestScore: Int = 0,
    val bestTotal: Int = 0,
    val averagePercent: Int = 0
)

data class PracticeSummary(
    val favoritesCount: Int = 0,
    val stats: QuizStats = QuizStats()
)
