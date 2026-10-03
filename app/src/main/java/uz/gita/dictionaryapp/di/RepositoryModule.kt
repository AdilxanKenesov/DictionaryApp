package uz.gita.dictionaryapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.gita.dictionaryapp.data.repository_impl.DictionaryRepositoryImpl
import uz.gita.dictionaryapp.data.repository_impl.FavoriteRepositoryImpl
import uz.gita.dictionaryapp.data.repository_impl.HistoryRepositoryImpl
import uz.gita.dictionaryapp.data.repository_impl.QuizRepositoryImpl
import uz.gita.dictionaryapp.data.repository_impl.SettingsRepositoryImpl
import uz.gita.dictionaryapp.data.repository_impl.SpeechRepositoryImpl
import uz.gita.dictionaryapp.domain.repository.DictionaryRepository
import uz.gita.dictionaryapp.domain.repository.FavoriteRepository
import uz.gita.dictionaryapp.domain.repository.HistoryRepository
import uz.gita.dictionaryapp.domain.repository.QuizRepository
import uz.gita.dictionaryapp.domain.repository.SettingsRepository
import uz.gita.dictionaryapp.domain.repository.SpeechRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindDictionaryRepository(impl: DictionaryRepositoryImpl): DictionaryRepository

    @Binds
    @Singleton
    fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository

    @Binds
    @Singleton
    fun bindHistoryRepository(impl: HistoryRepositoryImpl): HistoryRepository

    @Binds
    @Singleton
    fun bindQuizRepository(impl: QuizRepositoryImpl): QuizRepository

    @Binds
    @Singleton
    fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    fun bindSpeechRepository(impl: SpeechRepositoryImpl): SpeechRepository
}
