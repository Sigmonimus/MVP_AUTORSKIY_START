package com.example.mvp_autorskiy_start.data.repository

import android.content.Context
import com.example.mvp_autorskiy_start.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.util.Random

data class Quote(val text: String, val author: String)

object HomeDataRepository {

    private val gson = Gson()
    private val random = Random()
    private var quotes: List<Quote>? = null
    private var tips: List<String>? = null

    private fun loadQuotes(context: Context): List<Quote> {
        if (quotes == null) {
            val input = context.resources.openRawResource(R.raw.quotes)
            val reader = InputStreamReader(input)
            val type = object : TypeToken<List<Quote>>() {}.type
            quotes = gson.fromJson(reader, type)
            reader.close()
        }
        return quotes!!
    }

    private fun loadTips(context: Context): List<String> {
        if (tips == null) {
            val input = context.resources.openRawResource(R.raw.tips)
            val reader = InputStreamReader(input)
            val type = object : TypeToken<List<String>>() {}.type
            tips = gson.fromJson(reader, type)
            reader.close()
        }
        return tips!!
    }

    fun getRandomQuote(context: Context): Quote {
        val list = loadQuotes(context)
        return list[random.nextInt(list.size)]
    }

    fun getRandomTip(context: Context): String {
        val list = loadTips(context)
        return list[random.nextInt(list.size)]
    }
}