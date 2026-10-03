package uz.gita.dictionaryapp.domain.model

class NotEnoughWordsException(val required: Int) : Exception("At least $required words are required")
