package com.safewordapp.utils

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import com.safewordapp.R

object SoundManager {

    private var mediaPlayer: MediaPlayer? = null

    fun playSound(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, R.raw.alert_sound)
        mediaPlayer?.setOnCompletionListener {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0)
        }
        mediaPlayer?.start()
    }

    fun stopSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
