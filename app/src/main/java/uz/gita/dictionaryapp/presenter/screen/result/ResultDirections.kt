package uz.gita.dictionaryapp.presenter.screen.result

import uz.gita.dictionaryapp.domain.model.PracticeSource
import uz.gita.dictionaryapp.domain.model.QuizMode
import uz.gita.dictionaryapp.navigation.AppNavigator
import uz.gita.dictionaryapp.presenter.screen.detail.DetailScreen
import uz.gita.dictionaryapp.presenter.screen.quiz.QuizScreen
import javax.inject.Inject

class ResultDirections @Inject constructor(
    private val appNavigator: AppNavigator
) : ResultContract.Directions {
    override fun navigateToDetail(english: String) {
        appNavigator.navigateTo(DetailScreen(english))
    }

    override fun retry(source: PracticeSource, mode: QuizMode) {
        appNavigator.replaceTo(QuizScreen(source, mode))
    }

    override fun back() {
        appNavigator.back()
    }
}
