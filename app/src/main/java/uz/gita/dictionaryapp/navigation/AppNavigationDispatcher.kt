package uz.gita.dictionaryapp.navigation

import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.flow.MutableSharedFlow

object AppNavigationDispatcher : AppNavigator, AppNavigationHandler {

    // replay = 0: aks holda Activity qayta yaratilganda oxirgi navigatsiya qayta bajarilardi
    override val backStack = MutableSharedFlow<AppNavigationParam>(extraBufferCapacity = 16)

    private fun navigate(param: AppNavigationParam) {
        backStack.tryEmit(param)
    }

    override fun navigateTo(screen: Screen) = navigate {
        push(screen)
    }

    override fun replaceTo(screen: Screen) = navigate {
        replace(screen)
    }

    override fun replaceAll(screen: Screen) = navigate {
        this.replaceAll(screen)
    }

    override fun back() = navigate {
        pop()
    }
}
