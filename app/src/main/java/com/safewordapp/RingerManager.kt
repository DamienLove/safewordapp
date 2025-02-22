package com.safeword

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper

object RingerManager {

    fun setRingerToMaxAndRing(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Remember current volume
        val originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING)

        // Set volume to max
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
        audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, 0)

        // Play a short ring sound
        val mediaPlayer = MediaPlayer.create(context, android.R.raw.ring)
        mediaPlayer.start()

        // After 5 seconds, reset to original volume
        Handler(Looper.getMainLooper()).postDelayed({
            mediaPlayer.stop()
            mediaPlayer.release()
            audioManager.setStreamVolume(AudioManager.STREAM_RING, originalVolume, 0)
        }, 5000)
    }
}
