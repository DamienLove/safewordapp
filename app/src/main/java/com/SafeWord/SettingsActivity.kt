package com.SafeWord

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.safeword.R

/**
 * SettingsActivity allows users to configure SafeWord settings:
 * - Set two safe words
 * - Adjust voice recognition sensitivity
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var etSafeWord1: EditText
    private lateinit var etSafeWord2: EditText
    private lateinit var etSensitivity: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize SharedPreferences
        prefs = getSharedPreferences("SafeWordPrefs", MODE_PRIVATE)

        // Initialize views using findViewById
        etSafeWord1 = findViewById(R.id.etSafeWord1)
        etSafeWord2 = findViewById(R.id.etSafeWord2)
        etSensitivity = findViewById(R.id.etSensitivity)
        btnSave = findViewById(R.id.btnSave)

        // Load existing data into text fields
        etSafeWord1.setText(prefs.getString("SafeWord1", ""))
        etSafeWord2.setText(prefs.getString("SafeWord2", ""))
        etSensitivity.setText(prefs.getInt("Sensitivity", 50).toString())

        // Save button logic
        btnSave.setOnClickListener {
            val safeWord1 = etSafeWord1.text.toString().trim()
            val safeWord2 = etSafeWord2.text.toString().trim()
            val sensitivity = etSensitivity.text.toString().toIntOrNull() ?: 50

            if (safeWord1.isNotEmpty() && safeWord2.isNotEmpty() && sensitivity in 1..100) {
                prefs.edit().apply {
                    putString("SafeWord1", safeWord1)
                    putString("SafeWord2", safeWord2)
                    putInt("Sensitivity", sensitivity)
                    apply()
                }
                Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show()
                finish() // Return to MainActivity
            } else {
                Toast.makeText(this, "Please enter valid safe words and sensitivity (1-100)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onTestModeClick(view: View) {
        val intent = Intent(this, EmergencyHandlerService::class.java)
        intent.putExtra("detectedSafeWord", "Test Mode Triggered")
        startService(intent)
    }
}
