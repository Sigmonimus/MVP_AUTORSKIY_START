package com.example.mvp_autorskiy_start.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class Difficulty(val value: Int) {
    EASY(1),
    MEDIUM(2),
    HARD(3)
}

@Parcelize
data class Quiz(
    val id: Int,
    val title: String,
    val description: String,
    val difficulty: Difficulty,
    val questions: List<Question>,
    val passingScore: Int,
    var bestScore: Int = 0,
    var isCompleted: Boolean = false,
    var isUnlocked: Boolean = false
) : Parcelable