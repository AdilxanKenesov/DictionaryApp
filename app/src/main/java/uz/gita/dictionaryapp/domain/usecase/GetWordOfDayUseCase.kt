package uz.gita.dictionaryapp.domain.usecase

import uz.gita.dictionaryapp.domain.model.WordItem

interface GetWordOfDayUseCase {
    suspend operator fun invoke(): Result<WordItem?>
}
