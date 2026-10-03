package uz.gita.dictionaryapp.data.source.local.user.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.gita.dictionaryapp.data.source.local.user.entity.QuizResultEntity

@Dao
interface QuizResultDao {

    @Query("SELECT * FROM quiz_results ORDER BY createdAt DESC")
    fun getAll(): Flow<List<QuizResultEntity>>

    @Insert
    suspend fun insert(result: QuizResultEntity)
}
