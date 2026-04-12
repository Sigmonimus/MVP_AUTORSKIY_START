package com.example.mvp_autorskiy_start.data.repository

import com.example.mvp_autorskiy_start.App
import com.example.mvp_autorskiy_start.data.models.SavedEssay

object FavoritesRepository {

    suspend fun addFavoriteArgument(argumentId: Int) {
        val current = App.dataStoreManager.getFavoriteArguments().toMutableSet()
        current.add(argumentId)
        App.dataStoreManager.setFavoriteArguments(current)
    }

    suspend fun removeFavoriteArgument(argumentId: Int) {
        val current = App.dataStoreManager.getFavoriteArguments().toMutableSet()
        current.remove(argumentId)
        App.dataStoreManager.setFavoriteArguments(current)
    }

    suspend fun isArgumentFavorite(argumentId: Int): Boolean =
        App.dataStoreManager.getFavoriteArguments().contains(argumentId)

    suspend fun getFavoriteArguments(): Set<Int> =
        App.dataStoreManager.getFavoriteArguments()

    suspend fun saveEssay(essay: SavedEssay) {
        val current = App.dataStoreManager.getFavoriteEssays().toMutableList()
        current.add(0, essay.toJson())
        App.dataStoreManager.setFavoriteEssays(current)
    }

    suspend fun removeEssay(essayJson: String) {
        val current = App.dataStoreManager.getFavoriteEssays().toMutableList()
        current.remove(essayJson)
        App.dataStoreManager.setFavoriteEssays(current)
    }

    suspend fun getFavoriteEssays(): List<SavedEssay> =
        App.dataStoreManager.getFavoriteEssays().mapNotNull { SavedEssay.fromJson(it) }
}