package com.safewordapp

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.preference.PreferenceManager
import java.util.Locale

class VoiceRecognitionService : Service(), RecognitionListener {
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent

    override fun onCreate() {
        super.onCreate()
        initializeSpeechRecognizer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startListening()
        return START_STICKY
    }

    private fun initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        speechRecognizer.setRecognitionListener(this)
    }

    private fun startListening() {
        try {
            speechRecognizer.startListening(recognizerIntent)
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Speech service failure: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        matches?.let {
            for (detectedText in it) {
                checkSafeWord(detectedText)
            }
        }
        startListening() // Resume listening for continuous recognition
    }

    private fun checkSafeWord(detectedText: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val safeWord1 = prefs.getString("SafeWord1", "")
        val safeWord2 = prefs.getString("SafeWord2", "")

        val isSafeWord1 = detectedText.contains(safeWord1 ?: "", ignoreCase = true)
        val isSafeWord2 = detectedText.contains(safeWord2 ?: "", ignoreCase = true)

        if (isSafeWord1 || isSafeWord2) {
            val intent = Intent(this, EmergencyHandlerService::class.java).apply {
                putExtra("detectedSafeWord", if (isSafeWord1) safeWord1 else safeWord2)
            }
            startService(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // Implement other RecognitionListener methods as needed
    override fun onReadyForSpeech(params: Bundle?) {}
    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {}
    override fun onError(error: Int) {
        startListening() // Restart listening on error to maintain continuous recognition
    }
    override fun onPartialResults(partialResults: Bundle?) {}
    override fun onEvent(eventType: Int, params: Bundle?) {}
}
