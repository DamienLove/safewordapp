package com.safewordapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    companion object {
        private const val SEND_SMS_PERMISSION_CODE = 100
        private const val CALL_PHONE_PERMISSION_CODE = 101
    }

    /**
     * Called when the activity is starting. This is where most initialization should go:
     * calling `setContentView(int)` to inflate the activity's UI, using `findViewById(int)`
     * to programmatically interact with widgets in the UI, etc.
     *
     * This implementation does the following:
     * 1. Calls the superclass's `onCreate` method.
     * 2. Sets the content view to the layout specified by `R.layout.activity_main`.
     * 3. Checks for the `SEND_SMS` permission and requests it if not granted.
     * 4. Checks for the `CALL_PHONE` permission and requests it if not granted.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *     being shut down then this Bundle contains the data it most recently supplied in
     *     `onSaveInstanceState(Bundle)`.  Note: Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle? ) {
        super.onCreate(savedInstanceState)

        // Check permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), SEND_SMS_PERMISSION_CODE)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), CALL_PHONE_PERMISSION_CODE)
        }
    }

    // Handle permission results
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SEND_SMS_PERMISSION_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed
            } else {
                // Permission denied, show error or fallback
            }
        }
    }
}
