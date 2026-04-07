package com.example.mvp_autorskiy_start.utils

import android.content.Context
import android.media.SoundPool
import com.example.mvp_autorskiy_start.R

object SoundPlayer {
    private var soundPool: SoundPool? = null
    private var correctSoundId = 0
    private var wrongSoundId = 0

    fun init(context: Context) {
        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .build()
        correctSoundId = soundPool?.load(context, R.raw.correct, 1) ?: 0
        wrongSoundId = soundPool?.load(context, R.raw.wrong, 1) ?: 0
    }

    fun playCorrect() {
        soundPool?.play(correctSoundId, 1f, 1f, 1, 0, 1f)
    }

    fun playWrong() {
        soundPool?.play(wrongSoundId, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}