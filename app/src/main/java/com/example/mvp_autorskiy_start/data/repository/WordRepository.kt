package com.example.mvp_autorskiy_start.data.repository

import com.example.mvp_autorskiy_start.App

object WordRepository {

    suspend fun getSavedWordsSet(): Set<String> = App.dataStoreManager.getSavedWords()

    suspend fun addWord(word: String) {
        val current = App.dataStoreManager.getSavedWords().toMutableSet()
        current.add(word)
        App.dataStoreManager.setSavedWords(current)
    }

    suspend fun removeWord(word: String) {
        val current = App.dataStoreManager.getSavedWords().toMutableSet()
        current.remove(word)
        App.dataStoreManager.setSavedWords(current)
    }
}