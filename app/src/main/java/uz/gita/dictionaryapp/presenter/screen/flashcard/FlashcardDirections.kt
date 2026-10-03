package uz.gita.dictionaryapp.presenter.screen.flashcard

import uz.gita.dictionaryapp.navigation.AppNavigator
import javax.inject.Inject

class FlashcardDirections @Inject constructor(
    private val appNavigator: AppNavigator
) : FlashcardContract.Directions {
    override fun back() {
        appNavigator.back()
    }
}
