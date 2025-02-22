...
if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
} else {
    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
}private lateinit var fusedLocationClient: FusedLocationProviderClient

fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
val locationCallback = object: LocationCallback() {
    override fun onLocationResult(locationResult: LocationResult) {
        for (loc in locationResult.locations) {
            sendLocationToEmergencyContact(loc.latitude, loc.longitude)
        }
    }
}
fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())