package com.safewordapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EmergencyHandlerService : Service() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val detectedSafeWord = intent?.getStringExtra("detectedSafeWord") ?: return START_NOT_STICKY

        coroutineScope.launch {
            try {
                val prefs = getSharedPreferences("SafeWordPrefs", MODE_PRIVATE)
                val contacts = prefs.getStringSet("emergencyContacts", emptySet())?.toList() ?: emptyList()
                val location = getUserLocation()

                val message = if (location != null) {
                    "SafeWord '$detectedSafeWord' detected. Location: https://maps.google.com/?q=${location.latitude},${location.longitude}"
                } else {
                    "SafeWord '$detectedSafeWord' detected. Location unavailable."
                }

                escalateEmergency(contacts, message)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Emergency error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return START_NOT_STICKY
    }

    private suspend fun escalateEmergency(contacts: List<String>, message: String, attemptLimit: Int = 3) {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
            AudioManager.STREAM_RING,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
            0
        )

        contacts.forEach { contactNumber ->
            repeat(attemptLimit) {
                try {
                    val callIntent = Intent(Intent.ACTION_CALL).apply {
                        data = Uri.parse("tel:$contactNumber")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    if (ContextCompat.checkSelfPermission(this@EmergencyHandlerService, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        startActivity(callIntent)
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "Call Phone permission not granted.", Toast.LENGTH_SHORT).show()
                        }
                        return@forEach
                    }
                    delay(5000L)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        callEmergencyServices()
    }

    private fun callEmergencyServices() {
        val emergencyNumber = "911"
        val callIntent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$emergencyNumber")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent)
        } else {
            Toast.makeText(applicationContext, "Call Phone permission not granted.", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun sendEmergencySms(safeWord: String) {
        val prefs = getSharedPreferences("SafeWordPrefs", MODE_PRIVATE)
        val contacts = prefs.getStringSet("emergencyContacts", emptySet()) ?: emptySet()
        val location = getUserLocation()

        val message = if (location != null) {
            "SafeWord '$safeWord' detected. Location: https://maps.google.com/?q=${location.latitude},${location.longitude}"
        } else {
            "SafeWord '$safeWord' detected. Location unavailable."
        }

        contacts.forEach { phoneNumber ->
            try {
                SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Failed to send SMS to $phoneNumber", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getUserLocation(): Location? {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val provider = LocationManager.GPS_PROVIDER
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null
        }

        return try {
            locationManager.getLastKnownLocation(provider)
        } catch (e: SecurityException) {
            Toast.makeText(applicationContext, "Location permission denied.", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "EMERGENCY_CHANNEL",
                "Emergency Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for SafeWord emergency events"
            }

            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificat
