package uz.gita.dictionaryapp.presenter.screen.settings

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import uz.gita.dictionaryapp.R
import uz.gita.dictionaryapp.domain.model.Accent
import uz.gita.dictionaryapp.domain.model.AppLanguage
import uz.gita.dictionaryapp.domain.model.ThemeMode
import uz.gita.dictionaryapp.presenter.ui.components.rememberMessenger
import uz.gita.dictionaryapp.presenter.ui.components.rememberTabOptions
import uz.gita.dictionaryapp.presenter.ui.theme.Dimens
import uz.gita.dictionaryapp.presenter.ui.theme.DictionaryAppTheme

object SettingsScreen : Tab {

    private fun readResolve(): Any = SettingsScreen

    override val options: TabOptions
        @Composable
        get() = rememberTabOptions(
            index = 3,
            title = R.string.tab_settings,
            icon = Icons.Outlined.Settings,
            selectedIcon = Icons.Rounded.Settings
        )

    @Composable
    override fun Content() {
        val viewModel: SettingsContract.ViewModel = getViewModel<SettingsViewModel>()
        val uiState by viewModel.container.stateFlow.collectAsState()
        val messenger = rememberMessenger()

        LaunchedEffect(Unit) {
            viewModel.container.sideEffectFlow.collect { effect ->
                when (effect) {
                    is SettingsContract.SideEffect.ShowMessage -> messenger.show(effect.message)
                }
            }
        }
        SettingsContent(uiState = uiState, onEventDispatcher = viewModel::onEventDispatcher)
    }

    // Har qatorda faqat ikonka, qisqa nom va boshqaruv elementi
    @Composable
    fun SettingsContent(
        uiState: SettingsContract.UiState,
        onEventDispatcher: (SettingsContract.Intent) -> Unit
    ) {
        val settings = uiState.settings
        val isDark = settings.themeMode == ThemeMode.DARK

        Scaffold(contentWindowInsets = WindowInsets.statusBars) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimens.ScreenPadding, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = stringResource(R.string.tab_settings),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Card {
                    SettingRow(
                        icon = if (isDark) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                        title = stringResource(R.string.theme),
                        onClick = { onEventDispatcher(SettingsContract.Intent.SetDarkTheme(!isDark)) }
                    ) {
                        Switch(
                            checked = isDark,
                            onCheckedChange = { onEventDispatcher(SettingsContract.Intent.SetDarkTheme(it)) },
                            thumbContent = {
                                Icon(
                                    imageVector = if (isDark) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        )
                    }
                    RowDivider()
                    SettingRow(icon = Icons.Rounded.Translate, title = stringResource(R.string.language)) {
                        FlagSelector(
                            options = listOf(
                                FlagOption(AppLanguage.UZ, R.drawable.ic_flag_uz, stringResource(R.string.language_uz)),
                                FlagOption(AppLanguage.EN, R.drawable.ic_flag_gb, stringResource(R.string.language_en))
                            ),
                            selected = settings.language,
                            onSelect = { onEventDispatcher(SettingsContract.Intent.SetLanguage(it)) }
                        )
                    }
                }

                Card {
                    SettingRow(icon = Icons.Rounded.RecordVoiceOver, title = stringResource(R.string.accent)) {
                        FlagSelector(
                            options = listOf(
                                FlagOption(Accent.US, R.drawable.ic_flag_us, stringResource(R.string.accent_us_full)),
                                FlagOption(Accent.UK, R.drawable.ic_flag_gb, stringResource(R.string.accent_uk_full))
                            ),
                            selected = settings.accent,
                            onSelect = { onEventDispatcher(SettingsContract.Intent.SetAccent(it)) }
                        )
                    }
                    RowDivider()
                    SettingRow(icon = Icons.Rounded.Speed, title = stringResource(R.string.speech_rate)) {
                        Text(
                            text = stringResource(R.string.speech_rate_value, settings.speechRate),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = settings.speechRate,
                        onValueChange = { onEventDispatcher(SettingsContract.Intent.SetSpeechRate(it)) },
                        valueRange = 0.5f..1.5f,
                        steps = 9,
                        colors = SliderDefaults.colors(
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            activeTickColor = MaterialTheme.colorScheme.onPrimary,
                            inactiveTickColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(start = 64.dp, end = 16.dp, bottom = 6.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun Card(content: @Composable ColumnScope.() -> Unit) {
        Surface(
            shape = RoundedCornerShape(Dimens.LargeRadius),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(content = content)
        }
    }

    @Composable
    private fun RowDivider() {
        HorizontalDivider(
            modifier = Modifier.padding(start = 64.dp),
            color = MaterialTheme.colorScheme.outline
        )
    }

    @Composable
    private fun SettingRow(
        icon: ImageVector,
        title: String,
        onClick: (() -> Unit)? = null,
        trailing: @Composable () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(11.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            )
            trailing()
        }
    }

    private data class FlagOption<T>(
        val value: T,
        @DrawableRes val flag: Int,
        val label: String
    )

    // Tanlangan bayroq primary halqa bilan, qolganlari xira ko'rinadi
    @Composable
    private fun <T> FlagSelector(
        options: List<FlagOption<T>>,
        selected: T,
        onSelect: (T) -> Unit
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            options.forEach { option ->
                val isSelected = option.value == selected
                val ring by animateDpAsState(if (isSelected) 2.dp else 1.dp, label = "ring")
                val alpha by animateFloatAsState(if (isSelected) 1f else 0.45f, label = "alpha")
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(
                            ring,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            CircleShape
                        )
                        .clickable(role = Role.RadioButton) { onSelect(option.value) }
                        .semantics {
                            contentDescription = option.label
                            this.selected = isSelected
                        }
                        .padding(5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(option.flag),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(alpha)
                    )
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun SettingsContentPreview() {
        DictionaryAppTheme {
            SettingsContent(uiState = SettingsContract.UiState(), onEventDispatcher = {})
        }
    }
}
