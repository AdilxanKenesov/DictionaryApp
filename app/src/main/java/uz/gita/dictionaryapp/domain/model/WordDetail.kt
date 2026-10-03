package uz.gita.dictionaryapp.domain.model

data class WordDetail(
    val english: String,
    val transcript: String,
    val senses: List<Sense>,
    val definitions: List<DefinitionGroup>
)

data class Sense(
    val type: String,
    val uzbek: String,
    val countable: String?
)

data class DefinitionGroup(
    val type: String,
    val items: List<String>
)
