package uz.gita.dictionaryapp.presenter.screen.search

import uz.gita.dictionaryapp.navigation.AppNavigator
import uz.gita.dictionaryapp.presenter.screen.detail.DetailScreen
import javax.inject.Inject

class SearchDirections @Inject constructor(
    private val appNavigator: AppNavigator
) : SearchContract.Directions {
    override fun navigateToDetail(english: String) {
        appNavigator.navigateTo(DetailScreen(english))
    }
}
