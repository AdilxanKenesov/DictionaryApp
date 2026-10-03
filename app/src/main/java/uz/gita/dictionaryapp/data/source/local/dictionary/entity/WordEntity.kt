package uz.gita.dictionaryapp.data.source.local.dictionary.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Sxema tools/build_dictionary_db.py dagi bilan bir xil bo'lishi shart
@Entity(
    tableName = "words",
    indices = [Index("english"), Index("uzbek")]
)
data class WordEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(collate = ColumnInfo.NOCASE) val english: String,
    val type: String,
    val transcript: String,
    @ColumnInfo(collate = ColumnInfo.NOCASE) val uzbek: String,
    val countable: String?
)
