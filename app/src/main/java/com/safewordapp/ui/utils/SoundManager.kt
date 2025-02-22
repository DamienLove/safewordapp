package com.safewordapp.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer

private const val MEDIA_VOLUME_ADJUSTMENT_FLAGS = 0
object SoundManager {

    private var mediaPlayer: MediaPlayer? = null

    fun playSound(appContext: Context) {
        val systemAudioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val initialVolume = systemAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxSystemVolume = systemAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        systemAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxSystemVolume, AudioManager.FLAG_ALLOW_RINGER_MODES)

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(appContext, appContext.resources.getIdentifier("safe_word_sound", "raw", appContext.packageName))
        mediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )

        mediaPlayer?.setOnCompletionListener { player ->
            systemAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, initialVolume, AudioManager.FLAG_PLAY_SOUND)
            player.release()
            mediaPlayer = null
        }
        try {
            mediaPlayer?.start()
        } catch (e: Exception) {
            stopSound()
            e.printStackTrace()
        }
    }

    fun stopSound() {
        mediaPlayer?.let {
            it.stop()
            it.release()
            mediaPlayer = null
        }
    }
}