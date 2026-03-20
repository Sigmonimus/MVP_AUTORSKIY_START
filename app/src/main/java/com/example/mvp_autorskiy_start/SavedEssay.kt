package com.example.mvp_autorskiy_start.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SavedEssay(
    val id: String,
    val title: String,
    val content: String,
    val author: String,  // можно оставить "Моё сочинение"
    val theme: String,   // тема сочинения
    val date: Long
) : Parcelable