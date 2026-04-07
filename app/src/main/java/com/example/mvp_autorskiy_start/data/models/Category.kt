package com.example.mvp_autorskiy_start.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: Int,
    val name: String,
    val iconRes: Int, // идентификатор иконки (например, R.drawable.ic_love)
    val argumentIds: List<Int> // список id аргументов в этой категории
) : Parcelable