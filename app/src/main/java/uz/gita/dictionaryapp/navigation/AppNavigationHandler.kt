package uz.gita.dictionaryapp.navigation

import kotlinx.coroutines.flow.SharedFlow

interface AppNavigationHandler {

    val backStack: SharedFlow<AppNavigationParam>
}
