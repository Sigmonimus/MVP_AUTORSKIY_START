package com.example.mvp_autorskiy_start

import android.app.Application
import com.example.mvp_autorskiy_start.utils.MusicPlayerManager
import com.example.mvp_autorskiy_start.utils.SoundPlayer

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MusicPlayerManager.init(this)
        SoundPlayer.init(this)
    }
}