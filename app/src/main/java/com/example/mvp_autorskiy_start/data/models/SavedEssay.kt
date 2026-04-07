package com.example.mvp_autorskiy_start.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SavedEssay(
    val id: String,
    val title: String,
    val content: String,
    val author: String,
    val theme: String,
    val date: Long
) : Parcelable