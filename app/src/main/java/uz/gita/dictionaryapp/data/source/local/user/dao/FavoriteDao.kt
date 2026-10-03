package uz.gita.dictionaryapp.data.source.local.user.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.gita.dictionaryapp.data.source.local.user.entity.FavoriteEntity

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getAll(): Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM favorites")
    suspend fun getAllOnce(): List<FavoriteEntity>

    @Query("SELECT COUNT(*) FROM favorites")
    fun getCount(): Flow<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE english = :english)")
    fun isFavorite(english: String): Flow<Boolean>

    @Query("SELECT * FROM favorites WHERE english = :english")
    suspend fun getByEnglish(english: String): FavoriteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE english = :english")
    suspend fun delete(english: String)
}
