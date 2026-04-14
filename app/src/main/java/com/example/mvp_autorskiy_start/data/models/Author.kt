package com.example.mvp_autorskiy_start.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Author(
    val id: Int,
    val name: String,
    val years: String,
    val bio: String,
    val imageRes: String,
    val works: List<Work> = emptyList()
) : Parcelable