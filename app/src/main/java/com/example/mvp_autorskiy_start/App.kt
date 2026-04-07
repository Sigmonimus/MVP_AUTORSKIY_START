package com.example.mvp_autorskiy_start

import android.app.Application
import com.example.mvp_autorskiy_start.data.repository.ResourceMapper
import com.example.mvp_autorskiy_start.utils.MusicPlayerManager
import com.example.mvp_autorskiy_start.utils.SoundPlayer

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ResourceMapper.init(this)

        MusicPlayerManager.init(this)
        SoundPlayer.init(this)
    }
}