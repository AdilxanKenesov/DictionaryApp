package uz.gita.dictionaryapp

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import uz.gita.dictionaryapp.domain.model.AppLanguage
import uz.gita.dictionaryapp.domain.repository.SettingsRepository
import uz.gita.dictionaryapp.navigation.AppNavigationHandler
import uz.gita.dictionaryapp.presenter.screen.main.MainScreen
import uz.gita.dictionaryapp.presenter.ui.components.LocalMessenger
import uz.gita.dictionaryapp.presenter.ui.components.Messenger
import uz.gita.dictionaryapp.presenter.ui.theme.DictionaryAppTheme
import uz.gita.dictionaryapp.presenter.ui.theme.isDarkTheme
import uz.gita.dictionaryapp.utils.LocaleHelper
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigationHandler: AppNavigationHandler

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private lateinit var appliedLanguage: AppLanguage

    override fun attachBaseContext(newBase: Context) {
        appliedLanguage = LocaleHelper.savedLanguage(newBase)
        super.attachBaseContext(LocaleHelper.wrap(newBase, appliedLanguage))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by settingsRepository.settings.collectAsState()
            val darkTheme = settings.themeMode.isDarkTheme()

            // Til almashsa resurslar yangi tilda olinishi uchun Activity qayta yaratiladi
            LaunchedEffect(settings.language) {
                if (settings.language != appliedLanguage) recreate()
            }

            // Mavzu qo'lda tanlanganda status bar ikonkalari ham shunga moslashsin
            DisposableEffect(darkTheme) {
                val style = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme }
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
                onDispose { }
            }

            val scope = rememberCoroutineScope()
            val messenger = remember { Messenger(SnackbarHostState(), scope, this) }

            CompositionLocalProvider(LocalMessenger provides messenger) {
                DictionaryAppTheme(themeMode = settings.themeMode) {
                    Navigator(MainScreen()) { navigator ->
                        LaunchedEffect(navigator) {
                            navigationHandler.backStack.collectLatest { param ->
                                param.invoke(navigator)
                            }
                        }
                        SlideTransition(navigator)
                    }
                }
            }
        }
    }
}
