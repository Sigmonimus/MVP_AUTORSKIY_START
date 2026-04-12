package com.example.mvp_autorskiy_start.utils

import android.content.Context
import android.media.MediaPlayer

object MusicPlayerManager {

    private var mediaPlayer: MediaPlayer? = null
    private var currentTrackResId: Int = -1
    private var isInitialized = false

    fun init(context: Context) {
        if (!isInitialized) {
            isInitialized = true
        }
    }

    // Теперь вызывающий код (фрагмент) должен проверить настройки сам
    fun start(context: Context, trackResId: Int) {
        if (mediaPlayer?.isPlaying == true && currentTrackResId == trackResId) return

        currentTrackResId = trackResId
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, trackResId).apply {
            isLooping = true
            start()
        }
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun resume() {
        mediaPlayer?.start()
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true

    fun setVolume(left: Float, right: Float) {
        mediaPlayer?.setVolume(left, right)
    }
}