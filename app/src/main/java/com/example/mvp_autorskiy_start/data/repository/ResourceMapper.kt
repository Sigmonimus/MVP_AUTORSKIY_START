package com.example.mvp_autorskiy_start.data.repository

import android.content.Context
import android.util.Log
import com.example.mvp_autorskiy_start.R

object ResourceMapper {
    private var appContext: Context? = null
    private val cache = mutableMapOf<String, Int>()

    fun init(context: Context) {
        appContext = context.applicationContext
    }
    fun getDrawableResId(name: String): Int {
        val ctx = appContext
        if (ctx == null) {
            Log.e("ResourceMapper", "Not initialized. Call init() in App.")
            return R.drawable.ic_default_avatar
        }
        cache[name]?.let { return it }
        val resId = ctx.resources.getIdentifier(name, "drawable", ctx.packageName)
        val result = if (resId != 0) {
            resId
        } else {
            Log.w("ResourceMapper", "Drawable not found: $name, using default")
            R.drawable.ic_default_avatar
        }
        cache[name] = result
        return result
    }
}