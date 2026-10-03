package uz.gita.dictionaryapp.presenter.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import uz.gita.dictionaryapp.R

val Manrope = FontFamily(
    Font(R.font.manrope_600, FontWeight.SemiBold),
    Font(R.font.manrope_700, FontWeight.Bold),
    Font(R.font.manrope_800, FontWeight.ExtraBold)
)

private val Default = Typography()

// Sarlavhalar Manrope, oddiy matn tizim shrifti (IPA belgilarini to'g'ri chiqaradi)
val Typography = Typography(
    displaySmall = Default.displaySmall.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, fontSize = 38.sp),
    headlineLarge = Default.headlineLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold),
    headlineMedium = Default.headlineMedium.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold),
    headlineSmall = Default.headlineSmall.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp),
    titleLarge = Default.titleLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.Bold),
    titleMedium = Default.titleMedium.copy(fontFamily = Manrope, fontWeight = FontWeight.Bold),
    labelLarge = Default.labelLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.Bold),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.2.sp)
)
