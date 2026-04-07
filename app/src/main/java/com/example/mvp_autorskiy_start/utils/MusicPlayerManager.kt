package com.example.mvp_autorskiy_start.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.example.mvp_autorskiy_start.R

object MusicPlayerManager {
    data class Track(val resId: Int, val name: String)

    private val tracks = listOf(
        Track(R.raw.lofiroomcafe_blooming_serenity_lofi_chill_beat_352429, "Lo-Fi Chill Beat"),
        Track(R.raw.paulyudin_inspiring_485937, "Inspiring"),
        Track(R.raw.purrplecat_after_the_rain_360275, "After the Rain"),
        Track(R.raw.universfield_quiet_reverie_268020, "Quiet Reverie"),
        Track(R.raw.vicatestudio_relaxing_chillhop_main_vrsion_173929, "Relaxing Chillhop")
    )

    private var currentTrackIndex = 0
    private var mediaPlayer: MediaPlayer? = null
    private var playing = false
    private var enabled = true
    private var appContext: Context? = null
    private var onTrackChangedListener: ((Int, Track) -> Unit)? = null

    fun init(context: Context) {
        appContext = context.applicationContext
        loadPreferences()
        if (currentTrackIndex !in tracks.indices) currentTrackIndex = 0
        if (enabled) createPlayer(currentTrackIndex)
    }

    private fun createPlayer(index: Int): Boolean {
        if (index !in tracks.indices) {
            Log.e("MusicPlayer", "Invalid track index $index")
            return false
        }
        val track = tracks[index]
        try {
            mediaPlayer?.release()
            val player = MediaPlayer.create(appContext, track.resId)
            if (player == null) {
                Log.e("MusicPlayer", "Failed to create MediaPlayer for ${track.name} (resId=${track.resId})")
                mediaPlayer = null
                if (tracks.size > 1 && index != 0) {
                    Log.w("MusicPlayer", "Trying fallback to first track")
                    return createPlayer(0)
                }
                return false
            }
            player.isLooping = true
            player.setVolume(0.5f, 0.5f)
            player.setOnErrorListener { mp, what, extra ->
                Log.e("MusicPlayer", "MediaPlayer error: what=$what, extra=$extra")
                mp.release()
                mediaPlayer = null
                playing = false
                false
            }
            mediaPlayer = player
            currentTrackIndex = index
            if (enabled && playing) {
                start()
            }
            onTrackChangedListener?.invoke(index, track)
            Log.d("MusicPlayer", "Player created for track ${track.name}")
            return true
        } catch (e: Exception) {
            Log.e("MusicPlayer", "Error creating player for ${track.name}", e)
            mediaPlayer = null
            return false
        }
    }

    fun start() {
        if (!enabled) {
            Log.d("MusicPlayer", "start ignored, music disabled")
            return
        }
        if (mediaPlayer == null) {
            Log.w("MusicPlayer", "MediaPlayer is null, recreating...")
            createPlayer(currentTrackIndex)
            if (mediaPlayer == null) return
        }
        if (!playing) {
            try {
                mediaPlayer?.start()
                playing = true
                Log.d("MusicPlayer", "Started track ${tracks[currentTrackIndex].name}")
            } catch (e: Exception) {
                Log.e("MusicPlayer", "Start error", e)
                mediaPlayer?.release()
                mediaPlayer = null
                playing = false
            }
        }
    }

    fun pause() {
        // При выключении музыки мы будем полностью освобождать плеер,
        // поэтому этот метод будет вызываться только из setEnabled(false) и release().
        // Вручную его вызывать не нужно.
        if (mediaPlayer != null && playing) {
            try {
                mediaPlayer?.pause()
                playing = false
                Log.d("MusicPlayer", "Paused")
            } catch (e: Exception) {
                Log.e("MusicPlayer", "Pause error", e)
                mediaPlayer?.release()
                mediaPlayer = null
                playing = false
            }
        }
    }

    fun release() {
        try {
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e("MusicPlayer", "Release error", e)
        }
        mediaPlayer = null
        playing = false
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
        if (enabled) {
            // При включении создаём плеер, если его нет
            if (mediaPlayer == null) {
                createPlayer(currentTrackIndex)
            }
            start()
        } else {
            // При выключении полностью освобождаем плеер
            release()
        }
        savePreferences()
    }

    fun isEnabled(): Boolean = enabled

    fun setTrack(index: Int) {
        if (index !in tracks.indices) {
            Log.e("MusicPlayer", "Invalid track index $index")
            return
        }
        if (index == currentTrackIndex) {
            Log.d("MusicPlayer", "Track already playing")
            return
        }
        val wasPlaying = playing
        val oldIndex = currentTrackIndex
        val success = createPlayer(index)
        if (!success) {
            Log.w("MusicPlayer", "Failed to switch to track $index, restoring $oldIndex")
            createPlayer(oldIndex)
        } else {
            if (wasPlaying && enabled) {
                start()
            }
            saveTrackPreference(index)
        }
    }

    fun getCurrentTrack(): Track = tracks[currentTrackIndex]
    fun getTracks(): List<Track> = tracks
    fun getCurrentTrackIndex(): Int = currentTrackIndex

    fun setOnTrackChanged(listener: (Int, Track) -> Unit) {
        onTrackChangedListener = listener
    }

    private fun loadPreferences() {
        val prefs = appContext?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        enabled = prefs?.getBoolean("music_enabled", true) ?: true
        currentTrackIndex = prefs?.getInt("music_track", 0) ?: 0
        if (currentTrackIndex !in tracks.indices) currentTrackIndex = 0
    }

    private fun savePreferences() {
        appContext?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)?.edit()?.apply {
            putBoolean("music_enabled", enabled)
            putInt("music_track", currentTrackIndex)
            apply()
        }
    }

    private fun saveTrackPreference(index: Int) {
        appContext?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)?.edit()?.putInt("music_track", index)?.apply()
    }
}