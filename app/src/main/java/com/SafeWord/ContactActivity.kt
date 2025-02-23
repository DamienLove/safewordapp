package com.SafeWord

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.safeword.R

/**
 * ContactActivity allows users to save emergency contact information.
 * The contact's name, phone number, and email are stored in SharedPreferences.
 */
class ContactActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var etContactName: EditText
    private lateinit var etContactNumber: EditText
    private lateinit var etContactEmail: EditText
    private lateinit var btnSaveContact: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        // Initialize SharedPreferences
        prefs = getSharedPreferences("SafeWordPrefs", MODE_PRIVATE)

        // Initialize views using findViewById
        etContactName = findViewById(R.id.etContactName)
        etContactNumber = findViewById(R.id.etContactNumber)
        etContactEmail = findViewById(R.id.etContactEmail)
        btnSaveContact = findViewById(R.id.btnSaveContact)

        // Load existing contact data into text fields
        etContactName.setText(prefs.getString("ContactName", ""))
        etContactNumber.setText(prefs.getString("ContactNumber", ""))
        etContactEmail.setText(prefs.getString("ContactEmail", ""))

        // Save contact details when button is clicked
        btnSaveContact.setOnClickListener {
            val contactName = etContactName.text.toString().trim()
            val contactNumber = etContactNumber.text.toString().trim()
            val contactEmail = etContactEmail.text.toString().trim()

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
