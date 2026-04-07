package com.example.mvp_autorskiy_start.data.repository

import android.content.Context
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.models.Argument
import com.example.mvp_autorskiy_start.data.models.Category
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

object ArgumentsRepository {

    private val gson = Gson()
    private var cachedCategories: List<Category>? = null
    private var cachedArguments: List<Argument>? = null

    data class JsonData(
        val categories: List<JsonCategory>,
        val arguments: List<JsonArgument>
    )

    data class JsonCategory(
        val id: Int,
        val name: String,
        val icon: String,
        val argumentIds: List<Int>
    ) {
        fun toCategory(): Category = Category(
            id = id,
            name = name,
            iconRes = ResourceMapper.getDrawableResId(icon),
            argumentIds = argumentIds
        )
    }

    data class JsonArgument(
        val id: Int,
        val title: String,
        val workTitle: String,
        val author: String,
        val description: String,
        val fullText: String,
        val categoryIds: List<Int>,
        val image: String? = null
    ) {
        fun toArgument(): Argument = Argument(
            id = id,
            title = title,
            workTitle = workTitle,
            author = author,
            description = description,
            fullText = fullText,
            categoryIds = categoryIds,
            imageRes = if (image != null) ResourceMapper.getDrawableResId(image) else 0
        )
    }

    fun loadData(context: Context): Pair<List<Category>, List<Argument>> {
        if (cachedCategories != null && cachedArguments != null) {
            return Pair(cachedCategories!!, cachedArguments!!)
        }

        val inputStream = context.resources.openRawResource(R.raw.arguments)
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<JsonData>() {}.type
        val jsonData: JsonData = gson.fromJson(reader, type)
        reader.close()

        val categories = jsonData.categories.map { it.toCategory() }
        val arguments = jsonData.arguments.map { it.toArgument() }

        cachedCategories = categories
        cachedArguments = arguments

        return Pair(categories, arguments)
    }

    fun getCategories(context: Context): List<Category> = loadData(context).first
    fun getArguments(context: Context): List<Argument> = loadData(context).second
}