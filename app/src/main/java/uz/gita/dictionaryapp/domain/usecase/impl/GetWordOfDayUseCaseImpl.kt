package uz.gita.dictionaryapp.domain.usecase.impl

import uz.gita.dictionaryapp.domain.model.WordItem
import uz.gita.dictionaryapp.domain.repository.DictionaryRepository
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import uz.gita.dictionaryapp.domain.usecase.GetWordOfDayUseCase
import javax.inject.Inject

class GetWordOfDayUseCaseImpl @Inject constructor(
    private val repository: DictionaryRepository
) : GetWordOfDayUseCase {
    // Sana o'zgarmaguncha bir xil so'z chiqadi
    override suspend fun invoke(): Result<WordItem?> {
        val now = System.currentTimeMillis()
        val day = TimeUnit.MILLISECONDS.toDays(now + TimeZone.getDefault().getOffset(now))
        return repository.getWordOfDay(day)
    }
}
