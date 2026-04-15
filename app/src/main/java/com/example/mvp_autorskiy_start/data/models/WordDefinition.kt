package com.example.mvp_autorskiy_start.data.models

data class WordDefinition(
    val word: String,
    val meanings: List<Meaning>
)

data class Meaning(
    val partOfSpeech: String,
    val definitions: List<Definition>
)

data class Definition(
    val definition: String,
    val example: String? = null
)