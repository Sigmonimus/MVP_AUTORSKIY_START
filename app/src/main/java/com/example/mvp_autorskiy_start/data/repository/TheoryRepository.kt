package com.example.mvp_autorskiy_start.data.repository

import android.content.Context
import com.example.mvp_autorskiy_start.R
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.InputStreamReader

object TheoryRepository {

    private var cachedJson: JsonObject? = null

    private fun loadJson(context: Context): JsonObject {
        if (cachedJson == null) {
            val inputStream = context.resources.openRawResource(R.raw.theory_all)
            val reader = InputStreamReader(inputStream)
            cachedJson = JsonParser.parseReader(reader).asJsonObject
            reader.close()
        }
        return cachedJson!!
    }

    fun getMarkdown(context: Context, subsectionKey: String): String {
        val json = loadJson(context)
        return json.get(subsectionKey)?.asString ?: "Нет данных"
    }
}