package uz.gita.dictionaryapp.domain.usecase.impl

import uz.gita.dictionaryapp.domain.model.PracticeSource
import uz.gita.dictionaryapp.domain.model.QuizMode
import uz.gita.dictionaryapp.domain.model.QuizQuestion
import uz.gita.dictionaryapp.domain.repository.DictionaryRepository
import uz.gita.dictionaryapp.domain.usecase.GetPracticeWordsUseCase
import uz.gita.dictionaryapp.domain.usecase.GenerateQuizUseCase
import javax.inject.Inject

class GenerateQuizUseCaseImpl @Inject constructor(
    private val getPracticeWordsUseCase: GetPracticeWordsUseCase,
    private val dictionaryRepository: DictionaryRepository
) : GenerateQuizUseCase {
    override suspend fun invoke(source: PracticeSource, mode: QuizMode): Result<List<QuizQuestion>> =
        getPracticeWordsUseCase(source, QUESTION_COUNT).mapCatching { words ->
            words.map { word ->
                val translations = mode == QuizMode.EN_UZ
                val firstMeaning = word.uzbek.substringBefore("; ")
                val answer = if (translations) firstMeaning else word.english
                val distractors = dictionaryRepository
                    .getDistractors(word, translations, OPTION_COUNT - 1)
                    .getOrThrow()
                QuizQuestion(
                    english = word.english,
                    prompt = if (translations) word.english else firstMeaning,
                    type = word.type,
                    options = (distractors + answer).shuffled(),
                    answer = answer
                )
            }
        }

    companion object {
        const val QUESTION_COUNT = 10
        const val OPTION_COUNT = 4
    }
}
