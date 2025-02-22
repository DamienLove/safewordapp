if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.SEND_SMS), SEND_SMS_PERMISSION_CODE)
}val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0)private suspend fun escalateEmergency(contacts: List<String>, attemptLimit: Int = 3) {
    for (contact in contacts) {
        for (attempt in 1..attemptLimit) {
            try {
                // Call contact or send SMS
                SmsManager.getDefault().sendTextMessage(contact, null, message, null, null)
                delay(3000) // Wait before retry
                break
            } catch (e: Exception) {
                // Retry or continue to next contact
                if (attempt == attemptLimit) {
                    continue
                }
            }
        }
    }
    // If all fail, escalate to 9-1-1
    callEmergencyServices()
}

private fun callEmergencyServices() {
    val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:911"))
    startActivity(intent)
}package com.safewordapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class val notificationMessage = NotificationCompat.Builder(this, channelId)
    .setContentTitle("Emergency!")
    .setContentText("An emergency call is triggered.")
    .setPriority(NotificationCompat.PRIORITY_HIGH)
    .setCategory(Notification.CATEGORY_CALL)
    .build()EmergencyHandlerService : Service() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var notificationManager: NotificationManagerCompat

    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val detectedSafeWord = intent?.getStringExtra("detectedSafeWord") ?: return START_NOT_STICKY

        coroutineScope.launch {
            try {
                sendEmergencySms(detectedSafeWord)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Emergency error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return START_NOT_STICKY
    }



    private suspend fun sendEmergencySms(safeWord: String) {
        val prefs = getSharedPreferences("SafeWordPrefs", Context.MODE_PRIVATE)
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
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val provider = LocationManager.GPS_PROVIDER

        return try {
            locationManager.getLastKnownLocation(provider)
        } catch (e: SecurityException) {
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

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
