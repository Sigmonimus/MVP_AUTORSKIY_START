package com.example.mvp_autorskiy_start.data.repository

import android.content.Context
import com.example.mvp_autorskiy_start.App
import com.example.mvp_autorskiy_start.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

object HomeDataRepository {

    private data class Quote(val text: String, val author: String)

    fun getRandomQuote(context: Context): String {
        val json = context.resources.openRawResource(R.raw.quotes).bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Quote>>() {}.type
        val quotes: List<Quote> = Gson().fromJson(json, type) ?: emptyList()
        val random = quotes.randomOrNull() ?: return "Цитата не найдена"
        return "«${random.text}»\n— ${random.author}"
    }

    fun getRandomTip(context: Context): String {
        val json = context.resources.openRawResource(R.raw.tips).bufferedReader().use { it.readText() }
        // Исправлено: парсим как список строк
        val type = object : TypeToken<List<String>>() {}.type
        val tips: List<String> = Gson().fromJson(json, type) ?: emptyList()
        return if (tips.isNotEmpty()) tips.random() else "Совет дня отсутствует"
    }

    suspend fun updateStreak() {
        val prefs = App.dataStoreManager
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        val lastOpen = prefs.getLastOpenDate()

        if (lastOpen.isEmpty()) {
            prefs.setCurrentStreak(1)
            prefs.setBestStreak(1)
        } else {
            val lastDate = dateFormat.parse(lastOpen)
            val todayDate = dateFormat.parse(today)
            val diff = if (lastDate != null && todayDate != null) {
                (todayDate.time - lastDate.time) / (1000 * 60 * 60 * 24)
            } else {
                2L
            }

            when {
                diff == 0L -> return
                diff == 1L -> {
                    val newStreak = prefs.getCurrentStreak() + 1
                    prefs.setCurrentStreak(newStreak)
                    if (newStreak > prefs.getBestStreak()) {
                        prefs.setBestStreak(newStreak)
                    }
                }
                else -> prefs.setCurrentStreak(1)
            }
        }

        prefs.setLastOpenDate(today)
        val visited = prefs.getVisitedDates().toMutableSet()
        visited.add(today)
        prefs.setVisitedDates(visited)
    }

    suspend fun getCurrentStreak(): Int = App.dataStoreManager.getCurrentStreak()
    suspend fun getBestStreak(): Int = App.dataStoreManager.getBestStreak()
    suspend fun getVisitedDates(): Set<String> = App.dataStoreManager.getVisitedDates()
}