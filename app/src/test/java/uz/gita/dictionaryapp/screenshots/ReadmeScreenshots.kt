package uz.gita.dictionaryapp.screenshots

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import uz.gita.dictionaryapp.R
import uz.gita.dictionaryapp.domain.model.AppSettings
import uz.gita.dictionaryapp.domain.model.DefinitionGroup
import uz.gita.dictionaryapp.domain.model.FavoriteWord
import uz.gita.dictionaryapp.domain.model.PracticeSource
import uz.gita.dictionaryapp.domain.model.PracticeSummary
import uz.gita.dictionaryapp.domain.model.QuizMode
import uz.gita.dictionaryapp.domain.model.QuizQuestion
import uz.gita.dictionaryapp.domain.model.QuizStats
import uz.gita.dictionaryapp.domain.model.SearchDirection
import uz.gita.dictionaryapp.domain.model.Sense
import uz.gita.dictionaryapp.domain.model.ThemeMode
import uz.gita.dictionaryapp.domain.model.WordDetail
import uz.gita.dictionaryapp.domain.model.WordItem
import uz.gita.dictionaryapp.domain.model.WrongAnswer
import uz.gita.dictionaryapp.presenter.screen.detail.DetailContract
import uz.gita.dictionaryapp.presenter.screen.detail.DetailScreen
import uz.gita.dictionaryapp.presenter.screen.favorites.FavoritesContract
import uz.gita.dictionaryapp.presenter.screen.favorites.FavoritesScreen
import uz.gita.dictionaryapp.presenter.screen.flashcard.FlashcardContract
import uz.gita.dictionaryapp.presenter.screen.flashcard.FlashcardScreen
import uz.gita.dictionaryapp.presenter.screen.practice.PracticeContract
import uz.gita.dictionaryapp.presenter.screen.practice.PracticeScreen
import uz.gita.dictionaryapp.presenter.screen.quiz.QuizContract
import uz.gita.dictionaryapp.presenter.screen.quiz.QuizScreen
import uz.gita.dictionaryapp.presenter.screen.result.ResultScreen
import uz.gita.dictionaryapp.presenter.screen.search.SearchContract
import uz.gita.dictionaryapp.presenter.screen.search.SearchScreen
import uz.gita.dictionaryapp.presenter.screen.settings.SettingsContract
import uz.gita.dictionaryapp.presenter.screen.settings.SettingsScreen
import uz.gita.dictionaryapp.presenter.ui.theme.DictionaryAppTheme

/**
 * README uchun screenshotlar (test emas). Ma'lumotlar dictionary.db dan olingan haqiqiy so'zlar.
 *
 * Qayta yaratish:  ./gradlew :app:recordRoborazziDebug
 * Natija:          docs/screenshots/
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7, application = Application::class)
class ReadmeScreenshots {

    @get:Rule
    val composeRule = createComposeRule()

    private fun capture(name: String, theme: ThemeMode = ThemeMode.DARK, content: @Composable () -> Unit) {
        composeRule.setContent { DictionaryAppTheme(themeMode = theme) { content() } }
        composeRule.waitForIdle()
        composeRule.onRoot().captureRoboImage("../docs/screenshots/$name.png")
    }

    @Test
    fun search() = capture("01_search") { WithBottomBar(0) { SearchHome() } }

    @Test
    fun searchLight() = capture("01_search_light", ThemeMode.LIGHT) { WithBottomBar(0) { SearchHome() } }

    @Test
    fun searchResults() = capture("02_search_results") {
        WithBottomBar(0) {
            SearchScreen.SearchContent(
                uiState = SearchContract.UiState(query = "lig", results = lightWords),
                words = flowOf(PagingData.empty<SearchContract.ListItem>()).collectAsLazyPagingItems(),
                onEventDispatcher = {}
            )
        }
    }

    @Test
    fun searchUzbek() = capture("03_search_uz_en") {
        WithBottomBar(0) {
            SearchScreen.SearchContent(
                uiState = SearchContract.UiState(
                    query = "kitob",
                    direction = SearchDirection.UZ_EN,
                    results = listOf(
                        WordItem("book", "noun", "/bʊk/", "kitob"),
                        WordItem("pamphlet", "noun", "/pæm.flət/", "kitobcha"),
                        WordItem("reader", "noun", "/riː.dər/", "kitobxon"),
                        WordItem("paper-back", "noun", "/peɪ.pə.bæk/", "kitob jildi"),
                        WordItem("bookcase", "noun", "/bʊk.keɪs/", "kitob javoni")
                    )
                ),
                words = flowOf(PagingData.empty<SearchContract.ListItem>()).collectAsLazyPagingItems(),
                onEventDispatcher = {}
            )
        }
    }

    @Test
    fun detail() = capture("04_detail") { DetailOfLight() }

    @Test
    fun detailLight() = capture("04_detail_light", ThemeMode.LIGHT) { DetailOfLight() }

    @Test
    fun favorites() = capture("05_favorites") {
        WithBottomBar(1) {
            FavoritesScreen.FavoritesContent(
                uiState = FavoritesContract.UiState(
                    isLoading = false,
                    totalCount = 6,
                    favorites = listOf(
                        WordItem("courage", "noun", "/kʌr.ɪdʒ/", "jasorat; jasurlik"),
                        WordItem("beautiful", "adj", "/bju.tɪ.fəl/", "chiroyli"),
                        WordItem("discover", "verb", "/dɪskʌv.ər/", "kashf etmoq"),
                        WordItem("honest", "adj", "/ɒn.ɪst/", "rostgo'y"),
                        WordItem("journey", "noun", "/dʒɜ:.ni/", "sayohat; sayohat qilmoq"),
                        WordItem("knowledge", "noun", "/nɒl.ɪdʒ/", "bilim")
                    ).mapIndexed { i, word -> FavoriteWord(word, 10L - i) }
                ),
                onEventDispatcher = {}
            )
        }
    }

    @Test
    fun practice() = capture("06_practice") {
        WithBottomBar(2) {
            PracticeScreen.PracticeContent(
                uiState = PracticeContract.UiState(
                    source = PracticeSource.FAVORITES,
                    summary = PracticeSummary(favoritesCount = 24, stats = QuizStats(12, 10, 10, 78))
                ),
                onEventDispatcher = {}
            )
        }
    }

    @Test
    fun flashcard() = capture("07_flashcard") {
        FlashcardScreen(PracticeSource.FAVORITES).FlashcardContent(
            uiState = FlashcardContract.UiState(
                isLoading = false,
                queue = listOf(WordItem("discover", "verb", "/dɪskʌv.ər/", "kashf etmoq")),
                total = 10,
                known = 3
            ),
            onEventDispatcher = {}
        )
    }

    @Test
    fun quiz() = capture("08_quiz") {
        QuizScreen(PracticeSource.FAVORITES, QuizMode.EN_UZ).QuizContent(
            uiState = QuizContract.UiState(
                isLoading = false,
                questions = List(10) {
                    QuizQuestion("brave", "brave", "adj", listOf("chiroyli", "mard", "rostgo'y", "amalga oshmagan"), "mard")
                },
                index = 3,
                score = 3,
                selected = "chiroyli"
            ),
            onEventDispatcher = {}
        )
    }

    @Test
    fun result() = capture("09_result") {
        ResultScreen(
            score = 8,
            total = 10,
            wrongAnswers = arrayListOf(
                WrongAnswer("abandon", "abandon", "erishmoq", "tark qilmoq"),
                WrongAnswer("honest", "honest", "mard", "rostgo'y")
            ),
            source = PracticeSource.FAVORITES,
            mode = QuizMode.EN_UZ
        ).ResultContent(onEventDispatcher = {})
    }

    @Test
    fun settings() = capture("10_settings") {
        WithBottomBar(3) {
            SettingsScreen.SettingsContent(
                uiState = SettingsContract.UiState(AppSettings()),
                onEventDispatcher = {}
            )
        }
    }

    @Composable
    private fun SearchHome() {
        val entries = buildList<SearchContract.ListItem> {
            add(SearchContract.ListItem.Header("A", "h_a"))
            alphabetWords.forEach { add(SearchContract.ListItem.Word(it)) }
        }
        SearchScreen.SearchContent(
            uiState = SearchContract.UiState(
                wordOfDay = WordItem("courage", "noun", "/kʌr.ɪdʒ/", "jasorat; jasurlik"),
                history = listOf(
                    WordItem("light", "noun", "/laɪt/", "yorug'lik; yoritmoq"),
                    WordItem("journey", "noun", "/dʒɜ:.ni/", "sayohat"),
                    WordItem("achieve", "verb", "/ətʃiv/", "erishmoq"),
                    WordItem("brave", "adj", "/breɪv/", "mard")
                )
            ),
            words = flowOf(PagingData.from(entries)).collectAsLazyPagingItems(),
            onEventDispatcher = {}
        )
    }

    @Composable
    private fun DetailOfLight() {
        DetailScreen("light").DetailContent(
            uiState = DetailContract.UiState(
                isLoading = false,
                isFavorite = true,
                detail = WordDetail(
                    english = "light",
                    transcript = "/laɪt/",
                    senses = listOf(Sense("noun", "yorug'lik", "C/U"), Sense("verb", "yoritmoq", null)),
                    definitions = listOf(
                        DefinitionGroup(
                            "noun",
                            listOf(
                                "That agent, force, or action in nature by the operation of which upon the organs of sight, objects are rendered visible or luminous.",
                                "That which furnishes, or is a source of, light, as the sun, a star, a candle, a lighthouse, etc.",
                                "The time during which the light of the sun is visible; day; especially, the dawn of day."
                            ) + List(14) { "" }
                        ),
                        DefinitionGroup(
                            "verb",
                            listOf(
                                "To become ignited; to take fire; as, the match will not light.",
                                "To be illuminated; to receive light; to brighten; -- with up; as, the room lights up very well."
                            )
                        )
                    )
                )
            ),
            onEventDispatcher = {}
        )
    }

    // MainScreen'dagi pastki menyuning nusxasi (TabNavigator'siz chizish uchun)
    @Composable
    private fun WithBottomBar(selected: Int, content: @Composable () -> Unit) {
        val tabs = listOf(
            Triple(R.string.tab_dictionary, Icons.Outlined.AutoStories, Icons.Rounded.AutoStories),
            Triple(R.string.tab_favorites, Icons.Rounded.FavoriteBorder, Icons.Rounded.Favorite),
            Triple(R.string.tab_practice, Icons.Outlined.School, Icons.Rounded.School),
            Triple(R.string.tab_settings, Icons.Outlined.Settings, Icons.Rounded.Settings)
        )
        Scaffold(
            contentWindowInsets = WindowInsets(0),
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    tabs.forEachIndexed { index, (title, icon, selectedIcon) ->
                        NavigationBarItem(
                            selected = index == selected,
                            onClick = {},
                            icon = { Icon(if (index == selected) selectedIcon else icon, contentDescription = null) },
                            label = { Text(stringResource(title)) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) { content() }
        }
    }

    private val lightWords = listOf(
        WordItem("light", "noun", "/laɪt/", "yorug'lik; yoritmoq"),
        WordItem("lighten", "verb", "/laɪ.tən/", "yoritmoq"),
        WordItem("lighter", "noun", "/laɪ.tər/", "chaqmoq tosh"),
        WordItem("ligament", "noun", "/lɪg.ə.mənt/", "pay"),
        WordItem("lightning", "noun", "/laɪt.nɪŋ/", "chaqmoq"),
        WordItem("lighthouse", "noun", "/laɪt.haʊs/", "mayoq")
    )

    private val alphabetWords = listOf(
        WordItem("ably", "adv", "/eɪ.bli/", "moxirlik bilan"),
        WordItem("abnormal", "adj", "/æbnɔ.məl/", "normal bo'lmagan"),
        WordItem("abode", "noun", "/əbəʊd/", "turar joy"),
        WordItem("abolish", "verb", "/əbɒl.ɪʃ/", "to'xtatmoq"),
        WordItem("abominable", "adj", "/əbɒm.ɪ.nə.bl/", "jirkanch"),
        WordItem("aboriginal", "adj", "/æb.ərɪdʒ.ɪ.nəl/", "qadimiy; ibtidoiy"),
        WordItem("abound", "verb", "/əbaʊnd/", "ko'paymoq"),
        WordItem("about", "prep", "/əbaʊt/", "haqida; atrofida")
    )
}
