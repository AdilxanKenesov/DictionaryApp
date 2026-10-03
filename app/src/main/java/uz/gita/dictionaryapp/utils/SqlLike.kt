package uz.gita.dictionaryapp.utils

// LIKE ichida % va _ maxsus belgi, foydalanuvchi yozganini oddiy matn sifatida qidiramiz
fun String.escapeLike(): String =
    replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_")
