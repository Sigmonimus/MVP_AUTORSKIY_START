package com.example.mvp_autorskiy_start.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShuffledQuestion(
    val originalQuestion: Question,
    val shuffledOptions: List<String>,
    val correctAnswerIndex: Int
) : Parcelable