package com.example.mvp_autorskiy_start.data.models

data class SavedEssay(
    val title: String,
    val content: String,
    val date: String
) {
    fun toJson(): String = "$title|$content|$date"

    companion object {
        fun fromJson(json: String): SavedEssay? {
            val parts = json.split("|")
            return if (parts.size == 3) SavedEssay(parts[0], parts[1], parts[2]) else null
        }
    }
}