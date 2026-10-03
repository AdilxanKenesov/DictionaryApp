package uz.gita.dictionaryapp.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import uz.gita.dictionaryapp.domain.model.PracticeSummary
import kotlinx.coroutines.flow.combine
import uz.gita.dictionaryapp.domain.repository.FavoriteRepository
import uz.gita.dictionaryapp.domain.repository.QuizRepository
import uz.gita.dictionaryapp.domain.usecase.GetPracticeSummaryUseCase
import javax.inject.Inject

class GetPracticeSummaryUseCaseImpl @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val quizRepository: QuizRepository
) : GetPracticeSummaryUseCase {
    override fun invoke(): Flow<Result<PracticeSummary>> =
        combine(favoriteRepository.getCount(), quizRepository.getStats()) { count, stats ->
            stats.map { PracticeSummary(favoritesCount = count, stats = it) }
        }
}
