package com.example.mvp_autorskiy_start.network

import com.example.mvp_autorskiy_start.data.models.WordDefinition
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryApiService {
    @GET("api/v2/entries/en/{word}")
    suspend fun getDefinition(@Path("word") word: String): List<WordDefinition>
}