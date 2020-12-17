package com.rober.trashlocator.data.source.mapsmanager.utils.permissions

interface PermissionsUtils {
    var alreadyRequestLocationPermission: Boolean
    fun checkLocationPermission(): Boolean
    fun requestLocationPermissions()
    fun isLocationPermissionGranted(): Boolean
    fun setLocationPermissionGranted(isLocationPermissionGranted: Boolean)
}