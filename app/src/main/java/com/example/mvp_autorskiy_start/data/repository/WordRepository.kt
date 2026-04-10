package com.example.mvp_autorskiy_start.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class SavedWord(
    val word: String,
    val workId: Int,
    val workTitle: String,
    val timestamp: Long = System.currentTimeMillis()
)

object WordRepository {
    private const val PREFS_NAME = "word_prefs"
    private const val KEY_WORDS = "saved_words"
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveWord(word: String, workId: Int, workTitle: String) {
        val words = getWords().toMutableList()
        if (words.none { it.word.equals(word, ignoreCase = true) && it.workId == workId }) {
            words.add(SavedWord(word, workId, workTitle))
            saveWords(words)
        }
    }

    fun getWords(): List<SavedWord> {
        val json = prefs.getString(KEY_WORDS, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<SavedWord>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun removeWord(word: SavedWord) {
        val words = getWords().toMutableList()
        words.removeAll { it.word == word.word && it.workId == word.workId }
        saveWords(words)
    }

    private fun saveWords(words: List<SavedWord>) {
        val json = gson.toJson(words)
        prefs.edit().putString(KEY_WORDS, json).apply()
    }
}