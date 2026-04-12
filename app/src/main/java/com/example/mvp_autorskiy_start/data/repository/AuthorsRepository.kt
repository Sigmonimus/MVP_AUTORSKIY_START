package com.example.mvp_autorskiy_start.data.repository

import android.content.Context
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.models.Author
import com.example.mvp_autorskiy_start.utils.FB2Parser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object AuthorsRepository {

    private var authors: List<Author> = emptyList()

    fun loadAuthors(context: Context): List<Author> {
        if (authors.isNotEmpty()) return authors
        val jsonString = context.resources.openRawResource(R.raw.authors)
            .bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Author>>() {}.type
        authors = Gson().fromJson(jsonString, type)
        return authors
    }

    fun getAuthorById(context: Context, authorId: Int): Author? {
        return loadAuthors(context).find { it.id == authorId }
    }

    suspend fun loadFullText(context: Context, workId: Int): String = withContext(Dispatchers.IO) {
        val cacheFile = File(context.cacheDir, "work_$workId.html")
        if (cacheFile.exists()) return@withContext cacheFile.readText()

        val inputStream = context.assets.open("works/$workId.fb2")
        val rawHtml = FB2Parser.parseToHtml(inputStream)
        val words = WordRepository.getSavedWordsSet()
        val html = FB2Parser.highlightWords(rawHtml, words)
        cacheFile.writeText(html)
        html
    }
}