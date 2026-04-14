package com.example.mvp_autorskiy_start.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Work(
    val id: Int,
    val title: String,
    val summary: String,
    val fullText: String,
    val arguments: List<Argument> = emptyList()
) : Parcelable