package uz.gita.dictionaryapp.data.source.local.dictionary.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "definitions",
    indices = [Index("word")]
)
data class DefinitionEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(collate = ColumnInfo.NOCASE) val word: String,
    val type: String,
    val description: String
)
