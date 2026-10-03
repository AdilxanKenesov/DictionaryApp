package uz.gita.dictionaryapp.utils

import kotlin.coroutines.cancellation.CancellationException

// runCatching'dan farqi: CancellationException'ni yutib yubormaydi,
// aks holda coroutine bekor qilinganda ham Result.failure qaytib qolardi
inline fun <T> resultOf(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
