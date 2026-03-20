package com.example.mvp_autorskiy_start.data

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
    val categoryIds: List<Int>,
    val imageRes: Int = 0   // по умолчанию 0 – значит, нет картинки
) : Parcelable