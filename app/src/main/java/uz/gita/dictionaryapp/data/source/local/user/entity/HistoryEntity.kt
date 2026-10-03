package uz.gita.dictionaryapp.data.source.local.user.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey val english: String,
    val viewedAt: Long
)
