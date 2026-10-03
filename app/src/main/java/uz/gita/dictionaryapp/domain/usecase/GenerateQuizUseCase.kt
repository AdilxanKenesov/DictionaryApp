package uz.gita.dictionaryapp.domain.usecase

import uz.gita.dictionaryapp.domain.model.PracticeSource
import uz.gita.dictionaryapp.domain.model.QuizMode
import uz.gita.dictionaryapp.domain.model.QuizQuestion

interface GenerateQuizUseCase {
    suspend operator fun invoke(source: PracticeSource, mode: QuizMode): Result<List<QuizQuestion>>
}
