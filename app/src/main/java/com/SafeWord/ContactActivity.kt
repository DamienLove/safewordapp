package com.SafeWord

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.safeword.application.R
import org.json.JSONArray
import org.json.JSONObject

class ContactActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var etContactName: EditText
    private lateinit var etContactNumber: EditText
    private lateinit var etContactEmail: EditText
    private lateinit var btnSaveContact: Button

    private lateinit var contactList: MutableList<Contact>

data class Contact(
    val name: String,
    val number: String,
    val email: String
)
    private val CONTACT_LIST_KEY = "contact_list"
    private val PREFS_NAME = "SafeWordPrefs"
    private val TAG = "ContactActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        contactList = loadContactList()

        initializeViews()
        setupSaveButton()
    }

    private fun initializeViews() {
        etContactName = findViewById(R.id.etContactName)
        etContactNumber = findViewById(R.id.etContactNumber)
        etContactEmail = findViewById(R.id.etContactEmail)
        btnSaveContact = findViewById(R.id.btnSaveContact)
    }

    private fun addContact(contact: Contact) {
        contactList.add(contact)
        saveContactList()
        clearInputFields()
        Toast.makeText(this, "Contact added successfully.", Toast.LENGTH_SHORT).show()
    }

    private fun clearInputFields() {
        etContactName.text.clear()
        etContactNumber.text.clear()
        etContactEmail.text.clear()
    }

    private fun saveContactList() {
        val editor = prefs.edit()
        val json = convertContactListToJson(contactList)
        editor.putString(CONTACT_LIST_KEY, json)
        editor.apply()
    }

    private fun loadContactList(): MutableList<Contact> {
        val json = prefs.getString(CONTACT_LIST_KEY, null)
        return if (json != null) {
            convertJsonToContactList(json)
        } else {
            mutableListOf()
        }
    }

    private fun convertContactListToJson(contactList: MutableList<Contact>): String {
        val jsonArray = JSONArray()
        for (contact in contactList) {
            val jsonObject = JSONObject()
            jsonObject.put("name", contact.name)
            jsonObject.put("number", contact.number)
            jsonObject.put("email", contact.email)
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }

    private fun convertJsonToContactList(json: String): MutableList<Contact> {
        val contactList = mutableListOf<Contact>()
        val jsonArray = JSONArray(json)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val contact = Contact(
                jsonObject.getString("name"),
                jsonObject.getString("number"),
                jsonObject.getString("email")
            )
            contactList.add(contact)
        }
        return contactList
    }

    private fun setupSaveButton() {
        btnSaveContact.setOnClickListener {
            if (validateContact()) {
                addContact(getContact())
            } else {
                Toast.makeText(this, "Please fill in both name and number.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getContact(): Contact {
        val contactName = etContactName.text.toString().trim()
        val contactNumber = etContactNumber.text.toString().trim()
        val contactEmail = etContactEmail.text.toString().trim()
        return Contact(contactName, contactNumber, contactEmail)
    }

    fun validateContact(): Boolean {
        val contactName = etContactName.text.toString().trim()
        val contactNumber = etContactNumber.text.toString().trim()
        return contactName.isNotEmpty() && contactNumber.isNotEmpty()
    }
}