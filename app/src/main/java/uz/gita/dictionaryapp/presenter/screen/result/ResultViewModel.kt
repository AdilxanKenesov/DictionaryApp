package uz.gita.dictionaryapp.presenter.screen.result

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val directions: ResultContract.Directions
) : ViewModel(), ResultContract.ViewModel {

    override val container = orbitContainer<ResultContract.UiState, ResultContract.SideEffect>(
        ResultContract.UiState
    )

    override fun onEventDispatcher(intent: ResultContract.Intent) {
        when (intent) {
            is ResultContract.Intent.OpenWord -> intent { directions.navigateToDetail(intent.english) }
            is ResultContract.Intent.Retry -> intent { directions.retry(intent.source, intent.mode) }
            ResultContract.Intent.Home -> intent { directions.back() }
        }
    }
}
