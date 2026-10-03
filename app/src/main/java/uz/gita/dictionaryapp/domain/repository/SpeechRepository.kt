package uz.gita.dictionaryapp.domain.repository

import uz.gita.dictionaryapp.domain.model.Accent

interface SpeechRepository {

    // accent = null bo'lsa sozlamalardagi aksent ishlatiladi
    suspend fun speak(text: String, accent: Accent? = null): Result<Unit>
}
