package uz.gita.dictionaryapp.presenter.ui.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

// Tanlangan tabda to'ldirilgan, qolganlarida chiziqli ikonka
@Composable
fun Tab.rememberTabOptions(
    index: Int,
    @StringRes title: Int,
    icon: ImageVector,
    selectedIcon: ImageVector
): TabOptions {
    val selected = LocalTabNavigator.current.current.key == key
    val text = stringResource(title)
    val painter = rememberVectorPainter(if (selected) selectedIcon else icon)
    return remember(index, text, painter) {
        TabOptions(index = index.toUShort(), title = text, icon = painter)
    }
}
