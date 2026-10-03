package uz.gita.dictionaryapp.presenter.screen.quiz

import uz.gita.dictionaryapp.domain.model.PracticeSource
import uz.gita.dictionaryapp.domain.model.QuizMode
import uz.gita.dictionaryapp.domain.model.WrongAnswer
import uz.gita.dictionaryapp.navigation.AppNavigator
import uz.gita.dictionaryapp.presenter.screen.result.ResultScreen
import javax.inject.Inject

class QuizDirections @Inject constructor(
    private val appNavigator: AppNavigator
) : QuizContract.Directions {
    override fun back() {
        appNavigator.back()
    }

    // Natijadan "orqaga" bosilganda testga emas, Mashq bo'limiga qaytish uchun replace
    override fun navigateToResult(
        score: Int,
        total: Int,
        wrongAnswers: List<WrongAnswer>,
        source: PracticeSource,
        mode: QuizMode
    ) {
        appNavigator.replaceTo(ResultScreen(score, total, ArrayList(wrongAnswers), source, mode))
    }
}
