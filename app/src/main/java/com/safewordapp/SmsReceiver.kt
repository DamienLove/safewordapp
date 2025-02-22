package com.safeword

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.widget.Toast

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
        val bundle = intent.extras
        if (bundle != null) {
            val pdus = bundle.get("pdus") as? Array<*>
            pdus?.forEach { pdu ->
                val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
                val messageBody = smsMessage.messageBody
                val sender = smsMessage.originatingAddress

                // Retrieve stored safe words
                val safeWord1 = prefs.getString("SafeWord1", "") ?: ""
                val safeWord2 = prefs.getString("SafeWord2", "") ?: ""

                // If message contains either safe word, trigger ringer
                if (messageBody.contains(safeWord1, ignoreCase = true) ||
                    messageBody.contains(safeWord2, ignoreCase = true)) {

                    Toast.makeText(context, "SafeWord received from $sender", Toast.LENGTH_SHORT).show()

                    // Turn on ringer at max volume and ring
                    RingerManager.setRingerToMaxAndRing(context)
                }
            }
        }
    }
}
