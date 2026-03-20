package com.example.mvp_autorskiy_start.utils

import com.example.mvp_autorskiy_start.R
import android.util.Log

object ResourceMapper {
    fun getDrawableResId(name: String): Int {
        return when (name) {
            // Портреты авторов
            "pushkin_portrait" -> R.drawable.pushkin_portrait
            "tolstoy_portrait" -> R.drawable.tolstoy_portrait
            "dostoevsky_portrait" -> R.drawable.dostoevsky_portrait
            "bulgakov_portrait" -> R.drawable.bulgakov_portrait
            "solzhenitsyn_portrait" -> R.drawable.solzhenitsyn_portrait

            // Примеры для аргументов (раскомментируйте и добавьте нужные)
            "arg_101" -> R.drawable.arg_101
            "arg_103" -> R.drawable.arg_103
            "arg_104" -> R.drawable.arg_104
            "arg_106" -> R.drawable.arg_106
            "arg_108" -> R.drawable.arg_108
            // ...

            else -> {
                Log.e("ResourceMapper", "Unknown image name: $name")
                R.drawable.ic_default_argument
            }
        }
    }
}