package uz.gita.dictionaryapp.data.source.local.dictionary.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import uz.gita.dictionaryapp.data.source.local.dictionary.entity.DefinitionEntity
import uz.gita.dictionaryapp.data.source.local.dictionary.entity.WordEntity
import uz.gita.dictionaryapp.data.source.local.dictionary.entity.WordRow

@Dao
interface DictionaryDao {

    // Bosh sahifadagi "Barcha so'zlar" ro'yxati, Paging bilan bo'lib-bo'lib yuklanadi
    @Query(
        """
        SELECT english, MIN(type) AS type, MIN(transcript) AS transcript, GROUP_CONCAT(uzbek, '; ') AS uzbek
        FROM words GROUP BY english ORDER BY english
        """
    )
    fun getAllWords(): PagingSource<Int, WordRow>

    // Tartib: aniq moslik -> prefiks -> ichida uchrashi, keyin qisqa so'zlar oldin
    @Query(
        """
        SELECT english, MIN(type) AS type, MIN(transcript) AS transcript, GROUP_CONCAT(uzbek, '; ') AS uzbek
        FROM words
        WHERE english LIKE '%' || :query || '%' ESCAPE '\'
        GROUP BY english
        ORDER BY CASE
            WHEN english = :raw THEN 0
            WHEN english LIKE :query || '%' ESCAPE '\' THEN 1
            ELSE 2 END,
            LENGTH(english), english
        LIMIT :limit
        """
    )
    suspend fun searchEnglish(query: String, raw: String, limit: Int): List<WordRow>

    @Query(
        """
        SELECT english, type, transcript, uzbek
        FROM words
        WHERE uzbek LIKE '%' || :query || '%' ESCAPE '\'
        ORDER BY CASE
            WHEN uzbek = :raw THEN 0
            WHEN uzbek LIKE :query || '%' ESCAPE '\' THEN 1
            ELSE 2 END,
            LENGTH(uzbek), uzbek
        LIMIT :limit
        """
    )
    suspend fun searchUzbek(query: String, raw: String, limit: Int): List<WordRow>

    @Query("SELECT DISTINCT english FROM words WHERE english LIKE :prefix || '%' ESCAPE '\\' ORDER BY LENGTH(english), english LIMIT :limit")
    suspend fun getByPrefix(prefix: String, limit: Int): List<String>

    @Query("SELECT * FROM words WHERE english = :english ORDER BY id")
    suspend fun getWords(english: String): List<WordEntity>

    @Query("SELECT * FROM definitions WHERE word = :english ORDER BY id")
    suspend fun getDefinitions(english: String): List<DefinitionEntity>

    @Query(
        """
        SELECT english, MIN(type) AS type, MIN(transcript) AS transcript, GROUP_CONCAT(uzbek, '; ') AS uzbek
        FROM words WHERE english IN (:list) GROUP BY english
        """
    )
    suspend fun getRows(list: List<String>): List<WordRow>

    // Kun so'zi uchun: bitta so'zdan iborat, kamida 5 harfli so'zlar
    @Query("SELECT COUNT(DISTINCT english) FROM words WHERE english NOT LIKE '% %' AND LENGTH(english) >= 5")
    suspend fun getDailyCandidatesCount(): Int

    @Query(
        """
        SELECT english, MIN(type) AS type, MIN(transcript) AS transcript, GROUP_CONCAT(uzbek, '; ') AS uzbek
        FROM words WHERE english NOT LIKE '% %' AND LENGTH(english) >= 5
        GROUP BY english ORDER BY english LIMIT 1 OFFSET :offset
        """
    )
    suspend fun getDailyCandidate(offset: Int): WordRow?

    @Query(
        """
        SELECT english, MIN(type) AS type, MIN(transcript) AS transcript, GROUP_CONCAT(uzbek, '; ') AS uzbek
        FROM words WHERE english NOT LIKE '% %'
        GROUP BY english ORDER BY RANDOM() LIMIT :limit
        """
    )
    suspend fun getRandomRows(limit: Int): List<WordRow>

    @Query("SELECT * FROM words WHERE type = :type AND english != :english ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomByType(type: String, english: String, limit: Int): List<WordEntity>

    @Query("SELECT * FROM words WHERE english != :english ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomExcept(english: String, limit: Int): List<WordEntity>
}
