package uz.gita.dictionaryapp.presenter.screen.detail

import uz.gita.dictionaryapp.navigation.AppNavigator
import javax.inject.Inject

class DetailDirections @Inject constructor(
    private val appNavigator: AppNavigator
) : DetailContract.Directions {
    override fun back() {
        appNavigator.back()
    }
}
