package com.safewordapp

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.safeword.databinding.ActivityContactBinding

/**
 * ContactActivity allows users to save emergency contact information.
 * The contact's name, phone number, and email are stored in SharedPreferences.
 */
class ContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("SafeWordPrefs", MODE_PRIVATE)

        // Load existing contact data into text fields
        binding.etContactName.setText(prefs.getString("ContactName", ""))
        binding.etContactNumber.setText(prefs.getString("ContactNumber", ""))
        binding.etContactEmail.setText(prefs.getString("ContactEmail", ""))

        // Save contact details when button is clicked
        binding.btnSaveContact.setOnClickListener {
            val contactName = binding.etContactName.text.toString().trim()
            val contactNumber = binding.etContactNumber.text.toString().trim()
            val contactEmail = binding.etContactEmail.text.toString().trim()

            if (contactName.isNotEmpty() && contactNumber.isNotEmpty()) {
                prefs.edit().apply {
                    putString("ContactName", contactName)
                    putString("ContactNumber", contactNumber)
                    putString("ContactEmail", contactEmail)
                    apply()
                }
                Toast.makeText(this, "Contact saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please enter a name and phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
