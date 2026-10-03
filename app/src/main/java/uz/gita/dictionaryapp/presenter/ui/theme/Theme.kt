package uz.gita.dictionaryapp.presenter.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import uz.gita.dictionaryapp.domain.model.ThemeMode

private val LightColorScheme = lightColorScheme(
    primary = Indigo,
    onPrimary = Color.White,
    primaryContainer = IndigoContainer,
    onPrimaryContainer = OnIndigoContainer,
    secondary = Teal,
    onSecondary = Color.White,
    secondaryContainer = TealContainer,
    onSecondaryContainer = OnTealContainer,
    tertiary = Amber,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = OnAmberContainer,
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceContainerLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    surfaceContainerLowest = SurfaceLight,
    surfaceContainerLow = BackgroundLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighLight,
    outline = OutlineLight,
    outlineVariant = OutlineLight,
    error = ErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = ErrorLight,
    inverseSurface = OnSurfaceLight,
    inverseOnSurface = BackgroundLight,
    inversePrimary = IndigoDark
)

private val DarkColorScheme = darkColorScheme(
    primary = IndigoDark,
    onPrimary = OnIndigoDark,
    primaryContainer = IndigoContainerDark,
    onPrimaryContainer = IndigoContainer,
    secondary = TealDark,
    secondaryContainer = TealContainerDark,
    onSecondaryContainer = TealContainer,
    tertiary = AmberDark,
    tertiaryContainer = AmberContainerDark,
    onTertiaryContainer = AmberContainer,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceContainerDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    surfaceContainerLowest = BackgroundDark,
    surfaceContainerLow = BackgroundDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighDark,
    outline = OutlineDark,
    outlineVariant = OutlineDark,
    error = ErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = ErrorDark,
    inverseSurface = OnSurfaceDark,
    inverseOnSurface = BackgroundDark,
    inversePrimary = Indigo
)

// Material3 da "success" rangi yo'q, test javoblari uchun o'zimiz qo'shamiz
@Immutable
data class ExtraColors(
    val success: Color,
    val successContainer: Color
)

val LocalExtraColors = staticCompositionLocalOf {
    ExtraColors(success = SuccessLight, successContainer = SuccessContainerLight)
}

@Composable
fun DictionaryAppTheme(
    themeMode: ThemeMode = ThemeMode.LIGHT,
    content: @Composable () -> Unit
) {
    val darkTheme = themeMode.isDarkTheme()
    val extraColors = if (darkTheme) {
        ExtraColors(success = SuccessDark, successContainer = SuccessContainerDark)
    } else {
        ExtraColors(success = SuccessLight, successContainer = SuccessContainerLight)
    }

    CompositionLocalProvider(LocalExtraColors provides extraColors) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = Typography,
            content = content
        )
    }
}

fun ThemeMode.isDarkTheme(): Boolean = this == ThemeMode.DARK
