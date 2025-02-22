package com.safeword

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.safeword.databinding.ActivityContactBinding

class ContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("SafeWordPrefs", MODE_PRIVATE)

        // Load existing contact data
        binding.etContactName.setText(prefs.getString("ContactName", ""))
        binding.etContactNumber.setText(prefs.getString("ContactNumber", ""))
        binding.etContactEmail.setText(prefs.getString("ContactEmail", ""))

        binding.btnSaveContact.setOnClickListener {
            prefs.edit().apply {
                putString("ContactName", binding.etContactName.text.toString())
                putString("ContactNumber", binding.etContactNumber.text.toString())
                putString("ContactEmail", binding.etContactEmail.text.toString())
                apply()
            }
            finish()
        }
    }
}
