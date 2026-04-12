package com.example.mvp_autorskiy_start

import android.app.Application
import com.example.mvp_autorskiy_start.utils.DataStoreManager
import com.example.mvp_autorskiy_start.utils.MusicPlayerManager
import com.example.mvp_autorskiy_start.data.repository.ResourceMapper
import com.example.mvp_autorskiy_start.utils.SoundPlayer

class App : Application() {
    companion object {
        lateinit var dataStoreManager: DataStoreManager
            private set
    }

    override fun onCreate() {
        super.onCreate()
        dataStoreManager = DataStoreManager(applicationContext)
        ResourceMapper.init(this)
        MusicPlayerManager.init(this)
        SoundPlayer.init(this)
    }
}