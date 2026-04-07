package com.example.mvp_autorskiy_start.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Module(
    val id: Int,
    val title: String,
    val description: String,
    val iconRes: Int,
    val questions: List<Question>,
    val passingScore: Int,        // проходной балл в процентах (0-100)
    var isUnlocked: Boolean = false,
    var isCompleted: Boolean = false,
    var bestScore: Int = 0
) : Parcelable