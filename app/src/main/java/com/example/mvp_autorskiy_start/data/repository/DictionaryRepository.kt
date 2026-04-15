package com.example.mvp_autorskiy_start.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DictionaryRepository {
    private const val BASE_URL = "https://api.dictionaryapi.dev/"

    private val api: DictionaryApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DictionaryApiService::class.java)
    }

    suspend fun getDefinition(word: String): String {
        return try {
            val response = api.getDefinition(word)
            if (response.isNotEmpty()) {
                val firstMeaning = response[0].meanings.firstOrNull()
                val firstDefinition = firstMeaning?.definitions?.firstOrNull()
                firstDefinition?.definition ?: "Определение не найдено"
            } else {
                "Определение не найдено"
            }
        } catch (e: Exception) {
            "Не удалось загрузить определение"
        }
    }
}