package com.SafeWord

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.safeword.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * ContactActivity allows users to save emergency contact information.
 * The contact's name, phone number, and email are stored in SharedPreferences as a list.
 * Allows adding multiple contacts
 */
class ContactActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var etContactName: EditText
    private lateinit var etContactNumber: EditText
    private lateinit var etContactEmail: EditText

    // For managing multiple contacts
    private lateinit var contactList: MutableList<Contact>
    private val CONTACT_LIST_KEY = "contact_list"

    private lateinit var btnSaveContact: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        prefs = getSharedPreferences("SafeWordPrefs", MODE_PRIVATE)
        loadContactList()

        // Initialize views using findViewById
        etContactName = findViewById(R.id.etContactName)
        etContactNumber = findViewById(R.id.etContactNumber)
        etContactEmail = findViewById(R.id.etContactEmail)
        btnSaveContact = findViewById(R.id.btnSaveContact)


        // Save contact details when button is clicked
        btnSaveContact.setOnClickListener {
            val contactName = etContactName.text.toString().trim()
            val contactNumber = etContactNumber.text.toString().trim()
            val contactEmail = etContactEmail.text.toString().trim()

            if (contactName.isNotBlank() && contactNumber.isNotBlank()) {
                val newContact = Contact(contactName, contactNumber, contactEmail)
                contactList.add(newContact)
                saveContactList()

                // Check if the contact already has a safeword set
                val hasSafeword = prefs.contains("SafeWord_${contactNumber.replace(Regex("[^\\d]"), "")}")

                // If the contact doesn't have a safeword, invite them to set one
                if (!hasSafeword) {


                    Toast.makeText(this, "Inviting ${contactName} to set a SafeWord.", Toast.LENGTH_SHORT).show()

                    // Send an invitation to set a safeword (you can use Intent here to trigger an email or SMS sending process)
                    val inviteIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(contactEmail))
                        putExtra(Intent.EXTRA_SUBJECT, "SafeWord Invitation")
                        putExtra(Intent.EXTRA_TEXT, "Hi $contactName,\n\nYou've been added as a contact in SafeWord. Please set your SafeWord for enhanced security.\n\nThank you,\nThe SafeWord Team")
                    }
                    try {
                        startActivity(Intent.createChooser(inviteIntent, "Send invitation via:"))
                    } catch (e: Exception) {
                        Log.e("ContactActivity", "Error sending invitation: ${e.message}")
                        Toast.makeText(this, "Error sending invitation. Please check your email setup.", Toast.LENGTH_LONG).show()
                    }
                }

                // Clear input fields after saving
                etContactName.text.clear()
                etContactNumber.text.clear()
                etContactEmail.text.clear()

                Toast.makeText(this,"Contact added successfully",Toast.LENGTH_SHORT).show()

                //finish() //finish() will close the activity
            } else {
                Toast.makeText(this, "Please enter a name and phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// Define a data class for Contact
data class Contact(
    val name: String,
    val number: String,
    val email: String,
)

private fun ContactActivity.loadContactList() {
    val json = prefs.getString(CONTACT_LIST_KEY, null)
    contactList = if (json != null) {
        val type = object : TypeToken<MutableList<Contact>>() {}.type
        gson.fromJson(json, type)
    } else {
        mutableListOf()
    }
}

private fun ContactActivity.saveContactList() {
    val json = gson.toJson(contactList)
    prefs.edit().putString(CONTACT_LIST_KEY, json).apply()
}






