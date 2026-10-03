package uz.gita.dictionaryapp.presenter.screen.practice

import uz.gita.dictionaryapp.domain.model.PracticeSource
import uz.gita.dictionaryapp.domain.model.QuizMode
import uz.gita.dictionaryapp.navigation.AppNavigator
import uz.gita.dictionaryapp.presenter.screen.flashcard.FlashcardScreen
import uz.gita.dictionaryapp.presenter.screen.quiz.QuizScreen
import javax.inject.Inject

class PracticeDirections @Inject constructor(
    private val appNavigator: AppNavigator
) : PracticeContract.Directions {
    override fun navigateToFlashcards(source: PracticeSource) {
        appNavigator.navigateTo(FlashcardScreen(source))
    }

    override fun navigateToQuiz(source: PracticeSource, mode: QuizMode) {
        appNavigator.navigateTo(QuizScreen(source, mode))
    }
}
