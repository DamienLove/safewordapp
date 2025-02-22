package com.safewordapp

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class VoiceRecognitionService : Service() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        initializeSpeechRecognizer()
        Toast.makeText(this, "VoiceRecognitionService Started", Toast.LENGTH_SHORT).show()
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
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        speechRecognizer.startListening(intent)
    }

    private fun restartRecognition() {
        coroutineScope.launch {
            try {
                speechRecognizer.stopListening()
                startListening()
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Voice recognition error: ${e.message}", Toast.LENGTH_SHORT).show()
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
            val emergencyIntent = Intent(this, EmergencyHandlerService::class.java)
            emergencyIntent.putExtra("detectedSafeWord", detectedText)
            startService(emergencyIntent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startListening()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
        Toast.makeText(this, "VoiceRecognitionService Stopped", Toast.LENGTH_SHORT).show()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
