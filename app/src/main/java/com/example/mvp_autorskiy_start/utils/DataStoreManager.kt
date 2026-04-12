package com.example.mvp_autorskiy_start.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.mvp_autorskiy_start.data.models.PracticeDraft
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class DataStoreManager(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

    private val gson = Gson()

    private suspend fun <T> getValue(key: Preferences.Key<T>, default: T): T =
        context.dataStore.data.map { it[key] ?: default }.first()

    private suspend fun <T> setValue(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { it[key] = value }
    }

    // Streak
    suspend fun getCurrentStreak(): Int = getValue(Keys.CURRENT_STREAK, 0)
    suspend fun setCurrentStreak(value: Int) = setValue(Keys.CURRENT_STREAK, value)

    suspend fun getBestStreak(): Int = getValue(Keys.BEST_STREAK, 0)
    suspend fun setBestStreak(value: Int) = setValue(Keys.BEST_STREAK, value)

    suspend fun getLastOpenDate(): String = getValue(Keys.LAST_OPEN_DATE, "")
    suspend fun setLastOpenDate(value: String) = setValue(Keys.LAST_OPEN_DATE, value)

    suspend fun getVisitedDates(): Set<String> = getValue(Keys.VISITED_DATES, emptySet())
    suspend fun setVisitedDates(value: Set<String>) = setValue(Keys.VISITED_DATES, value)

    // Words
    suspend fun getSavedWords(): Set<String> = getValue(Keys.SAVED_WORDS, emptySet())
    suspend fun setSavedWords(value: Set<String>) = setValue(Keys.SAVED_WORDS, value)

    // Favorites
    suspend fun getFavoriteArguments(): Set<Int> {
        val json = getValue(Keys.FAVORITE_ARGUMENTS, "")
        return if (json.isBlank()) emptySet() else try {
            gson.fromJson(json, object : TypeToken<Set<Int>>() {}.type)
        } catch (e: Exception) { emptySet() }
    }
    suspend fun setFavoriteArguments(value: Set<Int>) {
        val json = gson.toJson(value)
        setValue(Keys.FAVORITE_ARGUMENTS, json)
    }

    suspend fun getFavoriteEssays(): List<String> {
        val json = getValue(Keys.FAVORITE_ESSAYS, "")
        return if (json.isBlank()) emptyList() else try {
            gson.fromJson(json, object : TypeToken<List<String>>() {}.type)
        } catch (e: Exception) { emptyList() }
    }
    suspend fun setFavoriteEssays(value: List<String>) {
        val json = gson.toJson(value)
        setValue(Keys.FAVORITE_ESSAYS, json)
    }

    // Quiz
    suspend fun getQuizScore(quizId: Int): Int = getValue(intPreferencesKey("quiz_score_$quizId"), 0)
    suspend fun setQuizScore(quizId: Int, score: Int) = setValue(intPreferencesKey("quiz_score_$quizId"), score)

    suspend fun isQuizCompleted(quizId: Int): Boolean = getValue(booleanPreferencesKey("quiz_completed_$quizId"), false)
    suspend fun setQuizCompleted(quizId: Int, value: Boolean) = setValue(booleanPreferencesKey("quiz_completed_$quizId"), value)

    suspend fun isQuizUnlocked(quizId: Int): Boolean = getValue(booleanPreferencesKey("quiz_unlocked_$quizId"), quizId == 1)
    suspend fun setQuizUnlocked(quizId: Int, value: Boolean) = setValue(booleanPreferencesKey("quiz_unlocked_$quizId"), value)

    // Profile
    suspend fun getUserName(): String = getValue(Keys.USER_NAME, "")
    suspend fun setUserName(value: String) = setValue(Keys.USER_NAME, value)

    suspend fun getUserEmail(): String = getValue(Keys.USER_EMAIL, "")
    suspend fun setUserEmail(value: String) = setValue(Keys.USER_EMAIL, value)

    suspend fun getAvatarUri(): String = getValue(Keys.AVATAR_URI, "")
    suspend fun setAvatarUri(value: String) = setValue(Keys.AVATAR_URI, value)

    suspend fun getAvatarResName(): String = getValue(Keys.AVATAR_RES_NAME, "pushkin_portrait")
    suspend fun setAvatarResName(value: String) = setValue(Keys.AVATAR_RES_NAME, value)

    suspend fun isNotificationsEnabled(): Boolean = getValue(Keys.NOTIFICATIONS_ENABLED, true)
    suspend fun setNotificationsEnabled(value: Boolean) = setValue(Keys.NOTIFICATIONS_ENABLED, value)

    // Practice
    suspend fun getPracticeDrafts(): Map<String, PracticeDraft> {
        val json = getValue(Keys.PRACTICE_DRAFTS, "")
        if (json.isBlank()) return emptyMap()
        return try {
            gson.fromJson(json, object : TypeToken<Map<String, PracticeDraft>>() {}.type) ?: emptyMap()
        } catch (e: Exception) {
            // Ошибка несовместимости типов (например, lastModified был строкой) – очищаем
            setPracticeDrafts(emptyMap())
            emptyMap()
        }
    }
    suspend fun setPracticeDrafts(value: Map<String, PracticeDraft>) {
        val json = gson.toJson(value)
        setValue(Keys.PRACTICE_DRAFTS, json)
    }

    suspend fun getTotalDraftsCount(): Int = getValue(Keys.TOTAL_DRAFTS_COUNT, 0)
    suspend fun setTotalDraftsCount(value: Int) = setValue(Keys.TOTAL_DRAFTS_COUNT, value)

    suspend fun getTotalWordsCount(): Int = getValue(Keys.TOTAL_WORDS_COUNT, 0)
    suspend fun setTotalWordsCount(value: Int) = setValue(Keys.TOTAL_WORDS_COUNT, value)

    // Music
    suspend fun isMusicEnabled(): Boolean = getValue(Keys.MUSIC_ENABLED, true)
    suspend fun setMusicEnabled(value: Boolean) = setValue(Keys.MUSIC_ENABLED, value)

    suspend fun getMusicTrack(): Int = getValue(Keys.MUSIC_TRACK, 0)
    suspend fun setMusicTrack(value: Int) = setValue(Keys.MUSIC_TRACK, value)

    private object Keys {
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val BEST_STREAK = intPreferencesKey("best_streak")
        val LAST_OPEN_DATE = stringPreferencesKey("last_open_date")
        val VISITED_DATES = stringSetPreferencesKey("visited_dates")
        val SAVED_WORDS = stringSetPreferencesKey("saved_words")
        val FAVORITE_ARGUMENTS = stringPreferencesKey("favorite_arguments")
        val FAVORITE_ESSAYS = stringPreferencesKey("favorite_essays")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val AVATAR_URI = stringPreferencesKey("avatar_uri")
        val AVATAR_RES_NAME = stringPreferencesKey("avatar_res_name")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val PRACTICE_DRAFTS = stringPreferencesKey("practice_drafts")
        val MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
        val MUSIC_TRACK = intPreferencesKey("music_track")
        val TOTAL_DRAFTS_COUNT = intPreferencesKey("total_drafts_count")
        val TOTAL_WORDS_COUNT = intPreferencesKey("total_words_count")
    }
}