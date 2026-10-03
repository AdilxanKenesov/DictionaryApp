package uz.gita.dictionaryapp.presenter.screen.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import uz.gita.dictionaryapp.presenter.screen.favorites.FavoritesScreen
import uz.gita.dictionaryapp.presenter.ui.components.AppSnackbarHost
import uz.gita.dictionaryapp.presenter.screen.practice.PracticeScreen
import uz.gita.dictionaryapp.presenter.screen.search.SearchScreen
import uz.gita.dictionaryapp.presenter.screen.settings.SettingsScreen

class MainScreen : Screen {

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {
        TabNavigator(SearchScreen) {
            Scaffold(
                contentWindowInsets = WindowInsets(0),
                snackbarHost = { AppSnackbarHost() },
                bottomBar = {
                    // Klaviatura ochiq paytda pastki menyu joy egallamasin
                    AnimatedVisibility(
                        visible = !WindowInsets.isImeVisible,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                            TabItem(SearchScreen)
                            TabItem(FavoritesScreen)
                            TabItem(PracticeScreen)
                            TabItem(SettingsScreen)
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    CurrentTab()
                }
            }
        }
    }

    @Composable
    private fun androidx.compose.foundation.layout.RowScope.TabItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current
        val options = tab.options
        NavigationBarItem(
            selected = tabNavigator.current.key == tab.key,
            onClick = { tabNavigator.current = tab },
            icon = { options.icon?.let { Icon(painter = it, contentDescription = null) } },
            label = { Text(options.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
