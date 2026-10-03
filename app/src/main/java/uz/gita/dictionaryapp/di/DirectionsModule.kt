package uz.gita.dictionaryapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.gita.dictionaryapp.presenter.screen.detail.DetailContract
import uz.gita.dictionaryapp.presenter.screen.detail.DetailDirections
import uz.gita.dictionaryapp.presenter.screen.favorites.FavoritesContract
import uz.gita.dictionaryapp.presenter.screen.favorites.FavoritesDirections
import uz.gita.dictionaryapp.presenter.screen.flashcard.FlashcardContract
import uz.gita.dictionaryapp.presenter.screen.flashcard.FlashcardDirections
import uz.gita.dictionaryapp.presenter.screen.practice.PracticeContract
import uz.gita.dictionaryapp.presenter.screen.practice.PracticeDirections
import uz.gita.dictionaryapp.presenter.screen.quiz.QuizContract
import uz.gita.dictionaryapp.presenter.screen.quiz.QuizDirections
import uz.gita.dictionaryapp.presenter.screen.result.ResultContract
import uz.gita.dictionaryapp.presenter.screen.result.ResultDirections
import uz.gita.dictionaryapp.presenter.screen.search.SearchContract
import uz.gita.dictionaryapp.presenter.screen.search.SearchDirections

@Module
@InstallIn(SingletonComponent::class)
interface DirectionsModule {

    @Binds
    fun bindSearchDirections(impl: SearchDirections): SearchContract.Directions

    @Binds
    fun bindDetailDirections(impl: DetailDirections): DetailContract.Directions

    @Binds
    fun bindFavoritesDirections(impl: FavoritesDirections): FavoritesContract.Directions

    @Binds
    fun bindPracticeDirections(impl: PracticeDirections): PracticeContract.Directions

    @Binds
    fun bindFlashcardDirections(impl: FlashcardDirections): FlashcardContract.Directions

    @Binds
    fun bindQuizDirections(impl: QuizDirections): QuizContract.Directions

    @Binds
    fun bindResultDirections(impl: ResultDirections): ResultContract.Directions
}
