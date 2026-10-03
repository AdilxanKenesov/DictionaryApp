package uz.gita.dictionaryapp.data.source.local.dictionary.entity

// Ro'yxatlar uchun: bir so'zning hamma ma'nolari bitta qatorga yig'ilgan
data class WordRow(
    val english: String,
    val type: String,
    val transcript: String,
    val uzbek: String
)
