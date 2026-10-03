package uz.gita.dictionaryapp.presenter.screen.favorites

import uz.gita.dictionaryapp.navigation.AppNavigator
import uz.gita.dictionaryapp.presenter.screen.detail.DetailScreen
import javax.inject.Inject

class FavoritesDirections @Inject constructor(
    private val appNavigator: AppNavigator
) : FavoritesContract.Directions {
    override fun navigateToDetail(english: String) {
        appNavigator.navigateTo(DetailScreen(english))
    }
}
