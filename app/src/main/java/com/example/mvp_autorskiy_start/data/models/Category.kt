package com.example.mvp_autorskiy_start.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: Int,
    val name: String,
    val iconRes: Int,
    val argumentIds: List<Int>
) : Parcelable