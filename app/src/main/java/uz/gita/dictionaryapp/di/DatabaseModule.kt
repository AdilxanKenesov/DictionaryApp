package uz.gita.dictionaryapp.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.gita.dictionaryapp.data.source.local.dictionary.dao.DictionaryDao
import uz.gita.dictionaryapp.data.source.local.dictionary.database.DictionaryDatabase
import uz.gita.dictionaryapp.data.source.local.user.dao.FavoriteDao
import uz.gita.dictionaryapp.data.source.local.user.dao.HistoryDao
import uz.gita.dictionaryapp.data.source.local.user.dao.QuizResultDao
import uz.gita.dictionaryapp.data.source.local.user.database.UserDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    // Versiya oshsa eski nusxa o'chiriladi va asset'dan yangisi olinadi
    @Provides
    @Singleton
    fun provideDictionaryDatabase(@ApplicationContext context: Context): DictionaryDatabase =
        Room.databaseBuilder(context, DictionaryDatabase::class.java, DictionaryDatabase.NAME)
            .createFromAsset(DictionaryDatabase.ASSET_PATH)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    @Singleton
    fun provideUserDatabase(@ApplicationContext context: Context): UserDatabase =
        Room.databaseBuilder(context, UserDatabase::class.java, UserDatabase.NAME).build()

    @Provides
    fun provideDictionaryDao(database: DictionaryDatabase): DictionaryDao = database.dictionaryDao()

    @Provides
    fun provideFavoriteDao(database: UserDatabase): FavoriteDao = database.favoriteDao()

    @Provides
    fun provideHistoryDao(database: UserDatabase): HistoryDao = database.historyDao()

    @Provides
    fun provideQuizResultDao(database: UserDatabase): QuizResultDao = database.quizResultDao()
}
