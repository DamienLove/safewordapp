package com.safeword

import android.content.Context
import android.media.AudioManager

object RingerManager {

    fun setRingerToMaxAndRing(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Remember current volume
        val originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING)

        // Set volume to max
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
        audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, 0)




        }
}
