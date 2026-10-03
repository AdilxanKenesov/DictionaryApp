package uz.gita.dictionaryapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.gita.dictionaryapp.domain.model.PracticeSummary

interface GetPracticeSummaryUseCase {
    operator fun invoke(): Flow<Result<PracticeSummary>>
}
