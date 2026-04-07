package com.example.mvp_autorskiy_start.data.repository

import android.content.Context
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.models.Argument
import com.example.mvp_autorskiy_start.data.models.Author
import com.example.mvp_autorskiy_start.data.models.Work
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
            imageRes = ResourceMapper.getDrawableResId(imageRes),
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
                it.toArgument(
                    authorName,
                    title
                )
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
            categoryIds = emptyList()
        )
    }
    fun loadFullText(context: Context, workId: Int): String {
        return try {
            val fileName = "work_$workId.txt"
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            "Полный текст не найден."
        }
    }
}