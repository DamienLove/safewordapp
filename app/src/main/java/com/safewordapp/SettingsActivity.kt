package com.safewordapp

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.safeword.databinding.ActivitySettingsBinding

/**
 * SettingsActivity allows users to configure SafeWord settings:
 * - Set two safe words
 * - Adjust voice recognition sensitivity
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("SafeWordPrefs", MODE_PRIVATE)

        // Load existing data into text fields
        binding.etSafeWord1.setText(prefs.getString("SafeWord1", ""))
        binding.etSafeWord2.setText(prefs.getString("SafeWord2", ""))
        binding.etSensitivity.setText(prefs.getInt("Sensitivity", 50).toString())

        // Save button logic
        binding.btnSave.setOnClickListener {
            val safeWord1 = binding.etSafeWord1.text.toString().trim()
            val safeWord2 = binding.etSafeWord2.text.toString().trim()
            val sensitivity = binding.etSensitivity.text.toString().toIntOrNull() ?: 50

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
}
