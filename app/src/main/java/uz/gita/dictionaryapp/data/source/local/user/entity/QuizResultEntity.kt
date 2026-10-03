package uz.gita.dictionaryapp.data.source.local.user.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val score: Int,
    val total: Int,
    val mode: String,
    val createdAt: Long
)
