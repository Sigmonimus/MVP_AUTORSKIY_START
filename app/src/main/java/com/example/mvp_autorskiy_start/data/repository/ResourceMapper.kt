package com.example.mvp_autorskiy_start.data.repository

import android.util.Log
import com.example.mvp_autorskiy_start.R

object ResourceMapper {
    fun getDrawableResId(name: String): Int {
        return when (name) {
            "gogol_portrait" -> R.drawable.gogol_portrait
            "turgenev_portrait" -> R.drawable.turgenev_portrait
            // "pushkin_portrait" -> R.drawable.pushkin_portrait
            // "tolstoy_portrait" -> R.drawable.tolstoy_portrait
            // "dostoevsky_portrait" -> R.drawable.dostoevsky_portrait
            // "bulgakov_portrait" -> R.drawable.bulgakov_portrait
            // "solzhenitsyn_portrait" -> R.drawable.solzhenitsyn_portrait
            // "kuprin_portrait" -> R.drawable.kuprin_portrait
            // "sholokhov_portrait" -> R.drawable.sholokhov_portrait
            // "astafiev_portrait" -> R.drawable.astafiev_portrait
            // "nekrasov_portrait" -> R.drawable.nekrasov_portrait
            // "leskov_portrait" -> R.drawable.leskov_portrait
            // "mayakovsky_portrait" -> R.drawable.mayakovsky_portrait
            // "chekhov_portrait" -> R.drawable.chekhov_portrait
            // "bunin_portrait" -> R.drawable.bunin_portrait
            // "saltykov_shchedrin_portrait" -> R.drawable.saltykov_shchedrin_portrait
            // "platonov_portrait" -> R.drawable.platonov_portrait
            // "fonvizin_portrait" -> R.drawable.fonvizin_portrait
            // "lermontov_portrait" -> R.drawable.lermontov_portrait
            // "gorky_portrait" -> R.drawable.gorky_portrait
            // "zamyatin_portrait" -> R.drawable.zamyatin_portrait
            // "rasputin_portrait" -> R.drawable.rasputin_portrait
            // "ostrovsky_portrait" -> R.drawable.ostrovsky_portrait
            // "griboyedov_portrait" -> R.drawable.griboyedov_portrait
            // "goncharov_portrait" -> R.drawable.goncharov_portrait
            // "vasiliev_portrait" -> R.drawable.vasiliev_portrait

            "arg_101" -> R.drawable.arg_101
            "arg_103" -> R.drawable.arg_103
            "arg_104" -> R.drawable.arg_104
            "arg_106" -> R.drawable.arg_106
            "arg_108" -> R.drawable.arg_108

            else -> {
                Log.e("ResourceMapper", "Unknown image name: $name")
                R.drawable.ic_default_argument
            }
        }
    }
}