package com.example.mvp_autorskiy_start.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Argument(
    val id: Int,
    val title: String,
    val description: String,
    val workTitle: String,
    val author: String,
    val fullText: String,
    val categoryIds: List<Int> = emptyList(),
    val imageRes: Int = 0
) : Parcelable