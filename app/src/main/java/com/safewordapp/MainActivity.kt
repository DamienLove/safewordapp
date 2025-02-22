package com.safewordapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.safeword.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences
        prefs = getSharedPreferences("SafeWordPrefs", Context.MODE_PRIVATE)

        // Toggle for enabling SafeWord engine
        binding.SafewordEnable.isChecked = prefs.getBoolean("isSafeWordEnabled", false)

        binding.SafewordEnable.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit {
                putBoolean("isSafeWordEnabled", isChecked)
                apply()
            }

            if (isChecked) {
                // Start voice recognition, SMS receiving, etc.
                ContextCompat.startForegroundService(
                    this, Intent(this, VoiceRecognitionService::class.java).setAction("START")
                )
                Toast.makeText(this, "SafeWord enabled", Toast.LENGTH_SHORT).show()
                testAppFunctionality()
            } else {
                // Stop voice recognition, etc.
                stopService(Intent(this, VoiceRecognitionService::class.java))
                Toast.makeText(this, "SafeWord disabled", Toast.LENGTH_SHORT).show()
            }
        }

        // Button: Record or set SafeWords
        binding.recordSafeWordsButton.setOnClickListener {
            // For simplicity, show a toast or open a dedicated activity/fragment
            Toast.makeText(this, "Would record SafeWords now", Toast.LENGTH_SHORT).show()
        }

        // Button: Navigate to Settings
        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // Button: Navigate to ContactActivity
        binding.contactButton.setOnClickListener {
            startActivity(Intent(this, ContactActivity::class.java))
        }

        // Button: Switch between incoming vs outgoing mode
        binding.switchModeButton.setOnClickListener {
            val isOutgoing = prefs.getBoolean("isOutgoingMode", false)
            val newMode = !isOutgoing
            prefs.edit {
                putBoolean("isOutgoingMode", newMode)
                apply()
            }
            Toast.makeText(
                this,
                if (newMode) "Switched to OUTGOING mode" else "Switched to INCOMING mode",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun testAppFunctionality() {
        // Example test: Check sensitivity or volume setting
        val prefs = getSharedPreferences("SafeWordPrefs", Context.MODE_PRIVATE)
        val sensitivity = prefs.getInt("Sensitivity", 50)
        // Perform a brief check without calling or sending texts
        Toast.makeText(this, "Testing app at sensitivity: $sensitivity", Toast.LENGTH_SHORT).show()
    }
}
