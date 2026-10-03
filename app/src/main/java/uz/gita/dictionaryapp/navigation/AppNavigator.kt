package uz.gita.dictionaryapp.navigation

import cafe.adriel.voyager.core.screen.Screen

interface AppNavigator {

    fun navigateTo(screen: Screen)

    fun replaceTo(screen: Screen)

    fun replaceAll(screen: Screen)

    fun back()
}
