package com.rober.trashlocator.data.source.mapsmanager.utils.permissions

interface PermissionsManager {
    var gpsEnabled: Boolean
    var alreadyRequestLocationPermission: Boolean
    fun checkLocationPermission(): Boolean
    fun checkLocationPermissionAndSettings(): Boolean
    fun requestLocationPermissions()
    fun isLocationPermissionGranted(): Boolean
    fun setLocationPermissionGranted(isLocationPermissionGranted: Boolean)
}