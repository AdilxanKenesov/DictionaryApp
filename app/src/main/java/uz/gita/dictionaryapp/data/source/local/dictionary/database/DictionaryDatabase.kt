package uz.gita.dictionaryapp.data.source.local.dictionary.database

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.gita.dictionaryapp.data.source.local.dictionary.dao.DictionaryDao
import uz.gita.dictionaryapp.data.source.local.dictionary.entity.DefinitionEntity
import uz.gita.dictionaryapp.data.source.local.dictionary.entity.WordEntity

// Faqat o'qish uchun, assets/database/dictionary.db dan nusxalanadi.
// Lug'at yangilansa: skriptda DB_VERSION va bu yerda version birga oshiriladi
@Database(entities = [WordEntity::class, DefinitionEntity::class], version = 1, exportSchema = false)
abstract class DictionaryDatabase : RoomDatabase() {

    abstract fun dictionaryDao(): DictionaryDao

    companion object {
        const val ASSET_PATH = "database/dictionary.db"
        const val NAME = "dictionary.db"
    }
}
