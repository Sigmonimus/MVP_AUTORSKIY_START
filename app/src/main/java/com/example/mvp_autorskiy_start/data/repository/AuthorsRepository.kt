package com.example.mvp_autorskiy_start.data.repository

import android.content.Context
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.models.Argument
import com.example.mvp_autorskiy_start.data.models.Author
import com.example.mvp_autorskiy_start.data.models.Work
import com.example.mvp_autorskiy_start.utils.FB2Parser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStreamReader

object AuthorsRepository {

    private val gson = Gson()
    private var cachedAuthors: List<Author>? = null

    fun getAuthors(context: Context): List<Author> {
        cachedAuthors?.let { return it }

        val inputStream = context.resources.openRawResource(R.raw.authors)
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<AuthorJson>>() {}.type
        val authorsJson: List<AuthorJson> = gson.fromJson(reader, type)
        reader.close()

        cachedAuthors = authorsJson.map { it.toAuthor() }
        return cachedAuthors!!
    }

    suspend fun loadFullText(context: Context, workId: Int): String = withContext(Dispatchers.IO) {
        try {
            val cacheFile = File(context.cacheDir, "work_$workId.html")
            if (cacheFile.exists()) return@withContext cacheFile.readText()

            val fileName = "works/$workId.fb2"
            val inputStream = context.assets.open(fileName)
            val fb2Content = inputStream.bufferedReader().use { it.readText() }
            val html = FB2Parser.parseToHtml(fb2Content) // здесь вызывается ваш парсер
            cacheFile.writeText(html)
            html
        } catch (e: Exception) {
            "<p>Текст не найден. Файл: works/$workId.fb2</p>"
        }
    }

    private data class AuthorJson(
        val id: Int,
        val name: String,
        val years: String,
        val bio: String,
        val imageRes: String,
        val works: List<WorkJson>
    ) {
        fun toAuthor(): Author = Author(
            id = id,
            name = name,
            years = years,
            bio = bio,
            imageRes = imageRes, // передаём строку
            works = works.map { it.toWork(name) }
        )
    }

    private data class WorkJson(
        val id: Int,
        val title: String,
        val summary: String,
        val fullText: String,
        val arguments: List<ArgumentJson>
    ) {
        fun toWork(authorName: String): Work = Work(
            id = id,
            title = title,
            summary = summary,
            fullText = fullText,
            arguments = arguments.map {
                it.toArgument(authorName, title)
            }
        )
    }

    private data class ArgumentJson(
        val id: Int,
        val title: String,
        val description: String
    ) {
        fun toArgument(authorName: String, workTitle: String): Argument = Argument(
            id = id,
            title = title,
            description = description,
            workTitle = workTitle,
            author = authorName,
            fullText = "",
            categoryIds = emptyList(),
            imageRes = 0
        )
    }
}