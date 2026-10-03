package uz.gita.dictionaryapp.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import uz.gita.dictionaryapp.domain.model.SearchDirection
import uz.gita.dictionaryapp.domain.model.WordDetail
import uz.gita.dictionaryapp.domain.model.WordItem

interface DictionaryRepository {

    fun getAllWords(): Flow<PagingData<WordItem>>

    suspend fun search(query: String, direction: SearchDirection): Result<List<WordItem>>

    suspend fun getSimilar(query: String): Result<List<String>>

    suspend fun getWordDetail(english: String): Result<WordDetail?>

    suspend fun getWordOfDay(dayIndex: Long): Result<WordItem?>

    suspend fun getWordItems(list: List<String>): Result<List<WordItem>>

    suspend fun getRandomWords(limit: Int): Result<List<WordItem>>

    suspend fun getDistractors(word: WordItem, translations: Boolean, count: Int): Result<List<String>>
}
