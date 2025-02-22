fun onTestModeClick(view: View) {
    val intent = Intent(this, EmergencyHandlerService::class.java)
    intent.putExtra("detectedSafeWord", "Test Mode Triggered")
    startService(intent)
}