package uz.gita.dictionaryapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uz.gita.dictionaryapp.domain.usecase.AddHistoryUseCase
import uz.gita.dictionaryapp.domain.usecase.GetAllWordsUseCase
import uz.gita.dictionaryapp.domain.usecase.ClearHistoryUseCase
import uz.gita.dictionaryapp.domain.usecase.GenerateQuizUseCase
import uz.gita.dictionaryapp.domain.usecase.GetFavoritesUseCase
import uz.gita.dictionaryapp.domain.usecase.GetHistoryUseCase
import uz.gita.dictionaryapp.domain.usecase.GetPracticeSummaryUseCase
import uz.gita.dictionaryapp.domain.usecase.GetPracticeWordsUseCase
import uz.gita.dictionaryapp.domain.usecase.GetSimilarWordsUseCase
import uz.gita.dictionaryapp.domain.usecase.GetWordDetailUseCase
import uz.gita.dictionaryapp.domain.usecase.GetWordOfDayUseCase
import uz.gita.dictionaryapp.domain.usecase.impl.AddHistoryUseCaseImpl
import uz.gita.dictionaryapp.domain.usecase.impl.GetAllWordsUseCaseImpl
import uz.gita.dictionaryapp.domain.usecase.impl.ClearHistoryUseCaseImpl
import uz.gita.dictionaryapp.domain.usecase.impl.GenerateQuizUseCaseImpl
import uz.gita.dictionaryapp.domain.usecase.impl.GetFavoritesUseCaseImpl
import uz.gita.dictionaryapp.domain.usecase.impl.GetHistoryUseCaseImpl
import uz.gita.dictionaryapp.domain.usecase.impl.GetPracticeSummaryUseCaseImpl
import uz.gita.dictionaryapp.domain.usecase.impl.GetPracticeWordsUseCaseImpl
import uz.gita.dictionaryapp.domain.usecase.impl.GetSimilarWordsUseCaseImpl
import uz.gita.dictionaryapp.domain.usecase.impl.GetWordDetailUseCaseImpl
import uz.gita.dictionaryapp.domain.usecase.impl.GetWordOfDayUseCaseImpl
import uz.gita.dictionaryapp.domain.usecase.impl.IsFavoriteUseCaseImpl
import uz.gita.dictionaryapp.domain.usecase.impl.RemoveFavoriteUseCaseImpl
import uz.gita.dictionaryapp.domain.usecase.impl.RemoveHistoryUseCaseImpl
import uz.gita.dictionaryapp.domain.usecase.impl.RestoreFavoriteUseCaseImpl
import uz.gita.dictionaryapp.domain.usecase.impl.SaveQuizResultUseCaseImpl
import uz.gita.dictionaryapp.domain.usecase.impl.SearchWordsUseCaseImpl
import uz.gita.dictionaryapp.domain.usecase.impl.SettingsUseCaseImpl
import uz.gita.dictionaryapp.domain.usecase.impl.SpeakUseCaseImpl
import uz.gita.dictionaryapp.domain.usecase.impl.ToggleFavoriteUseCaseImpl
import uz.gita.dictionaryapp.domain.usecase.IsFavoriteUseCase
import uz.gita.dictionaryapp.domain.usecase.RemoveFavoriteUseCase
import uz.gita.dictionaryapp.domain.usecase.RemoveHistoryUseCase
import uz.gita.dictionaryapp.domain.usecase.RestoreFavoriteUseCase
import uz.gita.dictionaryapp.domain.usecase.SaveQuizResultUseCase
import uz.gita.dictionaryapp.domain.usecase.SearchWordsUseCase
import uz.gita.dictionaryapp.domain.usecase.SettingsUseCase
import uz.gita.dictionaryapp.domain.usecase.SpeakUseCase
import uz.gita.dictionaryapp.domain.usecase.ToggleFavoriteUseCase

@Module
@InstallIn(ViewModelComponent::class)
interface UsecaseModule {
    @Binds
    fun bindAddHistory(impl: AddHistoryUseCaseImpl): AddHistoryUseCase

    @Binds
    fun bindClearHistory(impl: ClearHistoryUseCaseImpl): ClearHistoryUseCase

    @Binds
    fun bindGenerateQuiz(impl: GenerateQuizUseCaseImpl): GenerateQuizUseCase

    @Binds
    fun bindGetAllWords(impl: GetAllWordsUseCaseImpl): GetAllWordsUseCase

    @Binds
    fun bindGetFavorites(impl: GetFavoritesUseCaseImpl): GetFavoritesUseCase

    @Binds
    fun bindGetHistory(impl: GetHistoryUseCaseImpl): GetHistoryUseCase

    @Binds
    fun bindGetPracticeSummary(impl: GetPracticeSummaryUseCaseImpl): GetPracticeSummaryUseCase

    @Binds
    fun bindGetPracticeWords(impl: GetPracticeWordsUseCaseImpl): GetPracticeWordsUseCase

    @Binds
    fun bindGetSimilarWords(impl: GetSimilarWordsUseCaseImpl): GetSimilarWordsUseCase

    @Binds
    fun bindGetWordDetail(impl: GetWordDetailUseCaseImpl): GetWordDetailUseCase

    @Binds
    fun bindGetWordOfDay(impl: GetWordOfDayUseCaseImpl): GetWordOfDayUseCase

    @Binds
    fun bindIsFavorite(impl: IsFavoriteUseCaseImpl): IsFavoriteUseCase

    @Binds
    fun bindRemoveFavorite(impl: RemoveFavoriteUseCaseImpl): RemoveFavoriteUseCase

    @Binds
    fun bindRemoveHistory(impl: RemoveHistoryUseCaseImpl): RemoveHistoryUseCase

    @Binds
    fun bindRestoreFavorite(impl: RestoreFavoriteUseCaseImpl): RestoreFavoriteUseCase

    @Binds
    fun bindSaveQuizResult(impl: SaveQuizResultUseCaseImpl): SaveQuizResultUseCase

    @Binds
    fun bindSearchWords(impl: SearchWordsUseCaseImpl): SearchWordsUseCase

    @Binds
    fun bindSettings(impl: SettingsUseCaseImpl): SettingsUseCase

    @Binds
    fun bindSpeak(impl: SpeakUseCaseImpl): SpeakUseCase

    @Binds
    fun bindToggleFavorite(impl: ToggleFavoriteUseCaseImpl): ToggleFavoriteUseCase
}
