package uz.gita.dictionaryapp.data.source.local.user.database

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.gita.dictionaryapp.data.source.local.user.dao.FavoriteDao
import uz.gita.dictionaryapp.data.source.local.user.dao.HistoryDao
import uz.gita.dictionaryapp.data.source.local.user.dao.QuizResultDao
import uz.gita.dictionaryapp.data.source.local.user.entity.FavoriteEntity
import uz.gita.dictionaryapp.data.source.local.user.entity.HistoryEntity
import uz.gita.dictionaryapp.data.source.local.user.entity.QuizResultEntity

// Foydalanuvchi ma'lumotlari alohida DB da: lug'at yangilanganda o'chib ketmaydi
@Database(
    entities = [FavoriteEntity::class, HistoryEntity::class, QuizResultEntity::class],
    version = 1,
    exportSchema = false
)
abstract class UserDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao

    abstract fun historyDao(): HistoryDao

    abstract fun quizResultDao(): QuizResultDao

    companion object {
        const val NAME = "user.db"
    }
}
