package uz.gita.dictionaryapp.domain.usecase.impl

import uz.gita.dictionaryapp.domain.model.QuizMode
import uz.gita.dictionaryapp.domain.repository.QuizRepository
import uz.gita.dictionaryapp.domain.usecase.SaveQuizResultUseCase
import javax.inject.Inject

class SaveQuizResultUseCaseImpl @Inject constructor(
    private val repository: QuizRepository
) : SaveQuizResultUseCase {
    override suspend fun invoke(score: Int, total: Int, mode: QuizMode): Result<Unit> {
        if (total == 0) return Result.success(Unit)
        return repository.saveResult(score, total, mode)
    }
}
