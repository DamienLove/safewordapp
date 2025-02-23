package com.SafeWord

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.safeword.R

class MainActivity : AppCompatActivity() {
    companion object {
        private const val SEND_SMS_PERMISSION_CODE = 100
        private const val CALL_PHONE_PERMISSION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check and request necessary permissions
        checkAndRequestPermissions()

        // Enable or Disable Safe Word Listening
        val switchModeButton: Button = findViewById(R.id.switchModeButton)
        val prefs = getSharedPreferences("SafeWordPrefs", MODE_PRIVATE)
        val safeWordEnable = prefs.getBoolean("SafeWordEnable", false)
        switchModeButton.isSelected = safeWordEnable

        (switchModeButton as ToggleButton).setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("SafeWordEnable", isChecked).apply()
            if (isChecked) {
                // Start voice recognition service using foreground service
                val intent = Intent(this, VoiceRecognitionService::class.java)
                ContextCompat.startForegroundService(this, intent)
            } else {
                // Stop voice recognition service
                stopService(Intent(this, VoiceRecognitionService::class.java))
            }
        }

        // Navigate to settings screen
        findViewById<Button>(R.id.settingsButton).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // Navigate to contacts screen
        findViewById<Button>(R.id.contactButton).setOnClickListener {
            startActivity(Intent(this, ContactActivity::class.java))
        }

        // Switch between Incoming/Outgoing mode
        findViewById<Button>(R.id.switchModeButton).setOnClickListener {
            val isOutgoingMode = prefs.getBoolean("isOutgoingMode", false)
            prefs.edit().putBoolean("isOutgoingMode", !isOutgoingMode).apply()
        }

        // Record Safe Words action
        findViewById<Button>(R.id.recordSafeWordsButton).setOnClickListener {
            // Navigate to settings or voice configuration if required
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    /**
     * Check and request all necessary permissions.
     */
    private fun checkAndRequestPermissions() {
        val requiredPermissions = arrayOf(
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
        )
        val missingPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                missingPermissions.toTypedArray(),
                0 // Use an appropriate request code
            )
        }
    }

    // Handle the result for permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for ((index, result) in grantResults.withIndex()) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                // Notify the user that permission is required
                // You might choose to retry the permission request or inform the user that some features won't work.
            }
        }
    }
}