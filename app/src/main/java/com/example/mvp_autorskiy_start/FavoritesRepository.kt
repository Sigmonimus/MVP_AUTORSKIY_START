package com.example.mvp_autorskiy_start.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FavoritesRepository {

    private const val PREFS_NAME = "favorites_prefs"
    private const val KEY_FAVORITE_ARGUMENTS = "favorite_arguments"
    private const val KEY_FAVORITE_ESSAYS = "favorite_essays"

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // ---------- Аргументы ----------
    fun getFavoriteArguments(): Set<Int> {
        val json = prefs.getString(KEY_FAVORITE_ARGUMENTS, null)
        return if (json != null) {
            val type = object : TypeToken<HashSet<Int>>() {}.type
            gson.fromJson(json, type) ?: hashSetOf()
        } else {
            hashSetOf()
        }
    }

    fun addFavoriteArgument(argumentId: Int) {
        val current = getFavoriteArguments().toMutableSet()
        current.add(argumentId)
        saveFavoriteArguments(current)
    }

    fun removeFavoriteArgument(argumentId: Int) {
        val current = getFavoriteArguments().toMutableSet()
        current.remove(argumentId)
        saveFavoriteArguments(current)
    }

    private fun saveFavoriteArguments(ids: Set<Int>) {
        val json = gson.toJson(ids)
        prefs.edit().putString(KEY_FAVORITE_ARGUMENTS, json).apply()
    }

    fun isArgumentFavorite(argumentId: Int): Boolean = argumentId in getFavoriteArguments()

    // ---------- Сочинения ----------
    fun getSavedEssays(): List<SavedEssay> {
        val json = prefs.getString(KEY_FAVORITE_ESSAYS, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<SavedEssay>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun addSavedEssay(essay: SavedEssay) {
        val current = getSavedEssays().toMutableList()
        // предотвращаем дубликаты (по id)
        if (current.none { it.id == essay.id }) {
            current.add(essay)
            saveSavedEssays(current)
        }
    }

    fun removeSavedEssay(essayId: String) {
        val current = getSavedEssays().toMutableList()
        current.removeAll { it.id == essayId }
        saveSavedEssays(current)
    }

    private fun saveSavedEssays(essays: List<SavedEssay>) {
        val json = gson.toJson(essays)
        prefs.edit().putString(KEY_FAVORITE_ESSAYS, json).apply()
    }
}