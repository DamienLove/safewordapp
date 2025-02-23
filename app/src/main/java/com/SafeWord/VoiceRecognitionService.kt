package com.SafeWord

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class VoiceRecognitionService : Service() {
    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "VoiceRecognitionServiceChannel"
        const val TAG = "VoiceRecognitionService"
    }

    private lateinit var notification: Notification

    private lateinit var speechRecognizer: SpeechRecognizer
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        initializeSpeechRecognizer()
        createNotificationChannel()
        notification = createNotification()
        Toast.makeText(this, "VoiceRecognitionService Started", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Service Created")
    }

    private fun createNotification(): Notification {
        val builder = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Voice Recognition Service")
            .setContentText("Listening for safe words...")
            .setOngoing(true)

        return builder.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Voice Recognition Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                // Called when ready to receive speech input
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                // Restart recognition on error
                restartRecognition()
            }

            override fun onResults(results: Bundle?) {
                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let { matches ->
                    val detectedText = matches.firstOrNull()?.lowercase(Locale.getDefault()) ?: ""
                    checkSafeWord(detectedText)
                }
                restartRecognition()
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        speechRecognizer.startListening(intent)
    }

    private fun restartRecognition() {
        Log.d(TAG, "Restarting recognition")
        coroutineScope.launch {
            try {
                speechRecognizer.stopListening()
                startListening()
            } catch (e: Exception) {
                Toast.makeText(
                    applicationContext,
                    "Voice recognition error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkSafeWord(detectedText: String) {
        val prefs = getSharedPreferences("SafeWordPrefs", MODE_PRIVATE)
        val safeWord1 = prefs.getString("safeWord1", "")?.lowercase(Locale.getDefault())
        val safeWord2 = prefs.getString("safeWord2", "")?.lowercase(Locale.getDefault())

        if (detectedText == safeWord1 || detectedText == safeWord2) {
            Toast.makeText(this, "SafeWord Detected: $detectedText", Toast.LENGTH_SHORT).show()

            // Trigger emergency response here
            Log.d(TAG, "Safe word detected: $detectedText")
            val emergencyIntent = Intent(this, EmergencyHandlerService::class.java)
            emergencyIntent.putExtra("detectedSafeWord", detectedText)
            startService(emergencyIntent)
        }
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        this.startForeground(/* id = */ NOTIFICATION_ID, /* notification = */ notification)
        Log.d(TAG, "Service started in foreground")

        fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            startListening()
            return START_STICKY
        }

        fun onDestroy() {
            super.onDestroy()
            Log.d(TAG, "Service destroyed")
            speechRecognizer.destroy()
            stopForeground(true)
            Toast.makeText(this, "VoiceRecognitionService Stopped", Toast.LENGTH_SHORT).show()
        }

        fun onBind(intent: Intent?): IBinder? {
            return null
        }
        return TODO("Provide the return value")
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}

