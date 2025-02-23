package com.safewordapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.widget.Button
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat



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
        var SafeWordEnable = if (lastNonConfigurationInstance == 1) 1 else 0
        val toggleSafeWordButton: ToggleButton = findViewById(SafeWordEnable)
        toggleSafeWordButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Start voice recognition service
                val intent = Intent(this, VoiceRecognitionService::class.java)
                startService(intent)
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
            val prefs = getSharedPreferences("SafeWordPrefs", MODE_PRIVATE)
            val isOutgoingMode = prefs.getBoolean("isOutgoingMode", false)
            prefs.edit().putBoolean("isOutgoingMode", !isOutgoingMode).apply()
        }

        // Record Safe Words action
        findViewById<Button>(R.id.recordSafeWordsButton).setOnClickListener {
            // Navigate to settings or voice configuration if required
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), SEND_SMS_PERMISSION_CODE)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), CALL_PHONE_PERMISSION_CODE)
        }
    }

    // Handle permission results
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            SEND_SMS_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // SEND_SMS permission granted
                } else {
                    // Permission denied, handle accordingly
                }
            }
            CALL_PHONE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // CALL_PHONE permission granted
                } else {
                    // Permission denied, handle accordingly
                }
            }
        }
    }
}


