package uz.gita.dictionaryapp.data.source.local.user.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val english: String,
    val addedAt: Long
)
