package uz.gita.dictionaryapp.data.mapper

import uz.gita.dictionaryapp.data.source.local.dictionary.entity.DefinitionEntity
import uz.gita.dictionaryapp.data.source.local.dictionary.entity.WordEntity
import uz.gita.dictionaryapp.data.source.local.dictionary.entity.WordRow
import uz.gita.dictionaryapp.domain.model.DefinitionGroup
import uz.gita.dictionaryapp.domain.model.Sense
import uz.gita.dictionaryapp.domain.model.WordDetail
import uz.gita.dictionaryapp.domain.model.WordItem

fun WordRow.toWordItem(): WordItem = WordItem(
    english = english,
    type = type,
    transcript = transcript,
    uzbek = uzbek.split("; ").distinct().joinToString("; ")
)

fun WordEntity.toWordItem(): WordItem = WordItem(
    english = english,
    type = type,
    transcript = transcript,
    uzbek = uzbek
)

fun WordEntity.toSense(): Sense = Sense(
    type = type,
    uzbek = uzbek,
    countable = countable
)

// Bir so'zning qatorlari turkum bo'yicha tartiblanadi, izohlar ham turkum bo'yicha guruhlanadi
fun List<WordEntity>.toWordDetail(definitions: List<DefinitionEntity>): WordDetail = WordDetail(
    english = first().english,
    transcript = firstOrNull { it.transcript.isNotBlank() }?.transcript.orEmpty(),
    senses = map { it.toSense() },
    definitions = definitions
        .groupBy { it.type }
        .map { (type, items) -> DefinitionGroup(type = type, items = items.map { it.description }) }
)
