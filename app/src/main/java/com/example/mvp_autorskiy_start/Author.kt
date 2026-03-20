package com.example.mvp_autorskiy_start.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Author(
    val id: Int,
    val name: String,
    val years: String,
    val bio: String,
    val imageRes: Int,
    val works: List<Work>
) : Parcelable