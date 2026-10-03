package uz.gita.dictionaryapp.domain.model

// Ro'yxatlarda ko'rsatiladigan so'z: hamma ma'nolari "; " bilan birlashtirilgan
data class WordItem(
    val english: String,
    val type: String,
    val transcript: String,
    val uzbek: String
)
