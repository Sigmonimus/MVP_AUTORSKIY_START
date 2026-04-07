package com.example.mvp_autorskiy_start.data.models

data class PracticeDraft(
    val id: String,
    val title: String,
    val content: String,
    val theme: String = "",
    val lastModified: Long
)