package uz.gita.dictionaryapp.domain.usecase.impl

import uz.gita.dictionaryapp.domain.model.PracticeSource
import uz.gita.dictionaryapp.domain.model.WordItem
import kotlinx.coroutines.flow.first
import uz.gita.dictionaryapp.domain.model.NotEnoughWordsException
import uz.gita.dictionaryapp.domain.repository.DictionaryRepository
import uz.gita.dictionaryapp.domain.repository.FavoriteRepository
import uz.gita.dictionaryapp.domain.usecase.GetPracticeWordsUseCase
import javax.inject.Inject

class GetPracticeWordsUseCaseImpl @Inject constructor(
    private val dictionaryRepository: DictionaryRepository,
    private val favoriteRepository: FavoriteRepository
) : GetPracticeWordsUseCase {
    override suspend fun invoke(source: PracticeSource, limit: Int): Result<List<WordItem>> = when (source) {
        PracticeSource.RANDOM -> dictionaryRepository.getRandomWords(limit)
        PracticeSource.FAVORITES -> favoriteRepository.getFavorites().first().mapCatching { favorites ->
            if (favorites.size < MIN_FAVORITES) throw NotEnoughWordsException(MIN_FAVORITES)
            favorites.map { it.word }.shuffled().take(limit)
        }
    }

    companion object {
        const val MIN_FAVORITES = 4
    }
}
