  class VoiceRecognitionService : Service(), RecognitionListener {
      private lateinit var speechRecognizer: SpeechRecognizer
      private lateinit var recognizerIntent: Intent

      override fun onCreate() {
          super.onCreate()
          speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
          recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
              putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
              putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
          }
          speechRecognizer.setRecognitionListener(this)
      }

      override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
          startListening()
          return START_STICKY
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
          if (matches != null && matches.contains("YOUR SAFE WORD")) {
              // Process match
              val intent = Intent(this, EmergencyHandlerService::class.java)
              intent.putExtra("detectedSafeWord", "Safe Word Detected")
              startService(intent)
          }
          startListening() // Resume passive listening
      }

      override fun onDestroy() {
          super.onDestroy()
          speechRecognizer.destroy()
      }

      override fun onBind(intent: Intent?): IBinder? = null
  }