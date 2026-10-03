package uz.gita.dictionaryapp.domain.usecase

import uz.gita.dictionaryapp.domain.model.QuizMode

interface SaveQuizResultUseCase {
    suspend operator fun invoke(score: Int, total: Int, mode: QuizMode): Result<Unit>
}
