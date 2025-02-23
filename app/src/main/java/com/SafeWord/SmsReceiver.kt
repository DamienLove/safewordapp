package com.SafeWord

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.widget.Toast
import com.safeword.RingerManager

/**
 * SmsReceiver is a BroadcastReceiver that listens for incoming SMS messages.
 * It checks if the received message contains any of the pre-defined "safe words".
 * If a safe word is found, it triggers the device's ringer to play at maximum volume.
 *
 * This receiver is designed to be used in conjunction with an application that
 * allows users to set "safe words" and toggle between incoming and outgoing modes.
 *
 * The receiver will only process messages when not in "outgoing" mode, meaning it will
 * only react to incoming SMS messages.
 */
class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Check if we are in "incoming" mode
        val prefs = context.getSharedPreferences("SafeWordPrefs", Context.MODE_PRIVATE)
        val isOutgoingMode = prefs.getBoolean("isOutgoingMode", false)
        if (isOutgoingMode) {
            // If in outgoing mode, ignore incoming SMS
            return
        }

        // Extract SMS messages
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val pdus = bundle.get("pdus") as? Array<*>
            pdus?.forEach { pdu ->
                try {
                    val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray, bundle.getString("format"))
                    val messageBody = smsMessage.messageBody ?: return
                    val originatingAddress = smsMessage.originatingAddress

                    val safeWord1 = prefs.getString("SafeWord1", "") ?: ""
                    val safeWord2 = prefs.getString("SafeWord2", "") ?: ""

                    val isSafeWord1Present = messageBody.contains(safeWord1, ignoreCase = true)
                    val isSafeWord2Present = messageBody.contains(safeWord2, ignoreCase = true)

                    // If message contains either safe word, trigger ringer
                    if (isSafeWord1Present || isSafeWord2Present) {

                        Toast.makeText(context, "SafeWord received from $originatingAddress", Toast.LENGTH_SHORT).show()

                        // Trigger EmergencyHandlerService to notify contacts
                        val serviceIntent = Intent(context, EmergencyHandlerService::class.java).apply {
                            putExtra("detectedSafeWord", if (isSafeWord1Present) safeWord1 else safeWord2)
                        }


                        context.startService(serviceIntent)

                        // Turn on ringer at max volume and ring
                        RingerManager.setRingerToMaxAndRing(context)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
