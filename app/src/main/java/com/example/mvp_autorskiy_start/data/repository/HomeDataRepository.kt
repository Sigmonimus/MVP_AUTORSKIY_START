package com.example.mvp_autorskiy_start.data.repository

import android.content.Context
import com.example.mvp_autorskiy_start.App
import com.example.mvp_autorskiy_start.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

object HomeDataRepository {

    private data class Quote(val text: String, val author: String)

    fun getRandomQuote(context: Context): Pair<String, String> {
        val json = context.resources.openRawResource(R.raw.quotes).bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Quote>>() {}.type
        val quotes: List<Quote> = Gson().fromJson(json, type)
        val random = quotes.random()
        return Pair(random.text, random.author)
    }

    fun getRandomTip(context: Context): String {
        val json = context.resources.openRawResource(R.raw.tips).bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<String>>() {}.type
        val tips: List<String> = Gson().fromJson(json, type)
        return tips.random()
    }

    suspend fun updateStreak(): Boolean {
        val prefs = App.dataStoreManager
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        val lastOpen = prefs.getLastOpenDate()

        var shouldCelebrate = false
        val oldStreak = prefs.getCurrentStreak()

        if (lastOpen.isEmpty()) {
            prefs.setCurrentStreak(1)
            prefs.setBestStreak(1)
            shouldCelebrate = true
        } else {
            val lastDate = dateFormat.parse(lastOpen)
            val todayDate = dateFormat.parse(today)
            val diff = (todayDate.time - lastDate.time) / (1000 * 60 * 60 * 24)

            when {
                diff == 0L -> {
                    // ничего не делаем
                }
                diff == 1L -> {
                    val newStreak = oldStreak + 1
                    prefs.setCurrentStreak(newStreak)
                    if (newStreak > prefs.getBestStreak()) {
                        prefs.setBestStreak(newStreak)
                        shouldCelebrate = true
                    } else {
                        shouldCelebrate = true
                    }
                }
                else -> {
                    prefs.setCurrentStreak(1)
                }
            }
        }

        prefs.setLastOpenDate(today)
        val visited = prefs.getVisitedDates().toMutableSet()
        visited.add(today)
        prefs.setVisitedDates(visited)

        return shouldCelebrate
    }

    fun getCurrentStreak(): Int = runBlocking { App.dataStoreManager.getCurrentStreak() }
    fun getBestStreak(): Int = runBlocking { App.dataStoreManager.getBestStreak() }
    fun getVisitedDates(): Set<String> = runBlocking { App.dataStoreManager.getVisitedDates() }
}