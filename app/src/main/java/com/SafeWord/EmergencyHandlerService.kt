package com.SafeWord

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
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
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.safeword.application.R
import kotlinx.coroutines.*

class EmergencyHandlerService : Service() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val channelId = "EMERGENCY_CHANNEL"

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
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
            AudioManager.STREAM_RING,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
            0
        )

        contacts.forEach { contactNumber ->
            repeat(attemptLimit) {
                try {
                    sendSms(contactNumber, message)
                    delay(5000L)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        notifyUserToCallEmergencyServices()
    }

    private fun sendSms(phoneNumber: String, message: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null)
        } else {
            Toast.makeText(this, "SMS permission not granted.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun notifyUserToCallEmergencyServices() {
        val callIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:911")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            callIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_emergency)
            .setContentTitle("Emergency Detected")
            .setContentText("Tap to call emergency services.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
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
                channelId,
                "Emergency Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for SafeWord emergency events"
            }

            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
