package uz.gita.dictionaryapp.presenter.ui.components

import androidx.annotation.StringRes

// SideEffect orqali ko'rsatiladigan xabar: matn ViewModel'da emas, resursda turadi
data class UiMessage(
    @StringRes val resId: Int,
    val args: List<Any> = emptyList()
)
