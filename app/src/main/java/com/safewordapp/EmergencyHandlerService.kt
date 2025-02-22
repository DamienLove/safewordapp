package com.safewordapp

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

class EmergencyHandlerService : Service() {

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
