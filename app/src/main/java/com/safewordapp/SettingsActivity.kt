package com.safeword

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.safeword.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("SafeWordPrefs", MODE_PRIVATE)

        // Load existing data
        binding.etSafeWord1.setText(prefs.getString("SafeWord1", ""))
        binding.etSafeWord2.setText(prefs.getString("SafeWord2", ""))
        binding.etSensitivity.setText(prefs.getInt("Sensitivity", 50).toString())

        // Save button
        binding.btnSave.setOnClickListener {
            val safe1 = binding.etSafeWord1.text.toString()
            val safe2 = binding.etSafeWord2.text.toString()
            val sensitivity = binding.etSensitivity.text.toString().toIntOrNull() ?: 50

            prefs.edit().apply {
                putString("SafeWord1", safe1)
                putString("SafeWord2", safe2)
                putInt("Sensitivity", sensitivity)
                apply()
            }
            finish() // Go back to MainActivity
        }
    }
}
