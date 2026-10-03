package uz.gita.dictionaryapp.data.repository_impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.gita.dictionaryapp.data.mapper.toWordDetail
import uz.gita.dictionaryapp.data.mapper.toWordItem
import uz.gita.dictionaryapp.data.source.local.dictionary.dao.DictionaryDao
import uz.gita.dictionaryapp.domain.model.SearchDirection
import uz.gita.dictionaryapp.domain.model.WordDetail
import uz.gita.dictionaryapp.domain.model.WordItem
import uz.gita.dictionaryapp.domain.repository.DictionaryRepository
import uz.gita.dictionaryapp.utils.escapeLike
import uz.gita.dictionaryapp.utils.resultOf
import javax.inject.Inject

class DictionaryRepositoryImpl @Inject constructor(
    private val dao: DictionaryDao
) : DictionaryRepository {

    override fun getAllWords(): Flow<PagingData<WordItem>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false)
    ) {
        dao.getAllWords()
    }.flow.map { paging -> paging.map { it.toWordItem() } }

    override suspend fun search(query: String, direction: SearchDirection): Result<List<WordItem>> = resultOf {
        val escaped = query.escapeLike()
        val rows = when (direction) {
            SearchDirection.EN_UZ -> dao.searchEnglish(escaped, query, SEARCH_LIMIT)
            SearchDirection.UZ_EN -> dao.searchUzbek(escaped, query, SEARCH_LIMIT)
        }
        rows.map { it.toWordItem() }
    }

    override suspend fun getSimilar(query: String): Result<List<String>> = resultOf {
        dao.getByPrefix(query.take(SIMILAR_PREFIX).escapeLike(), SIMILAR_LIMIT)
    }

    override suspend fun getWordDetail(english: String): Result<WordDetail?> = resultOf {
        val words = dao.getWords(english)
        if (words.isEmpty()) null else words.toWordDetail(dao.getDefinitions(english))
    }

    override suspend fun getWordOfDay(dayIndex: Long): Result<WordItem?> = resultOf {
        val count = dao.getDailyCandidatesCount()
        if (count == 0) return@resultOf null
        // Ketma-ket kunlarda alifbo bo'yicha qo'shni so'zlar chiqmasligi uchun katta tub songa ko'paytiramiz
        val offset = ((dayIndex * DAY_STEP) % count).toInt()
        dao.getDailyCandidate(offset)?.toWordItem()
    }

    override suspend fun getWordItems(list: List<String>): Result<List<WordItem>> = resultOf {
        // SQLite parametrlar soni cheklangan, shuning uchun bo'lib so'raymiz
        val rows = list.chunked(CHUNK_SIZE).flatMap { dao.getRows(it) }
            .associateBy { it.english.lowercase() }
        list.mapNotNull { rows[it.lowercase()]?.toWordItem() }
    }

    override suspend fun getRandomWords(limit: Int): Result<List<WordItem>> = resultOf {
        dao.getRandomRows(limit).map { it.toWordItem() }
    }

    override suspend fun getDistractors(
        word: WordItem,
        translations: Boolean,
        count: Int
    ): Result<List<String>> = resultOf {
        val correct = if (translations) word.uzbek.split("; ") else listOf(word.english)
        val candidates = dao.getRandomByType(word.type, word.english, count * 4) +
                dao.getRandomExcept(word.english, count * 4)
        candidates
            .map { if (translations) it.uzbek else it.english }
            .filter { option -> correct.none { it.equals(option, ignoreCase = true) } }
            .distinctBy { it.lowercase() }
            .take(count)
    }

    private companion object {
        const val PAGE_SIZE = 50
        const val SEARCH_LIMIT = 60
        const val SIMILAR_PREFIX = 3
        const val SIMILAR_LIMIT = 6
        const val CHUNK_SIZE = 500
        const val DAY_STEP = 7919L
    }
}
