package com.rober.trashlocator.data.source.mapsmanager.utils.permissionsmanager

import com.rober.trashlocator.data.source.mapsmanager.utils.permissions.PermissionsManager

class FakePermissionsManager: PermissionsManager {
    override var alreadyRequestLocationPermission: Boolean = false
    private var returnPermissionsOk = false

    override fun checkLocationPermission(): Boolean {
        return returnPermissionsOk
    }

    fun setReturnPermissionsOk(value: Boolean){
        returnPermissionsOk = value
    }

    override fun requestLocationPermissions() {}

    override fun isLocationPermissionGranted(): Boolean {
        return true
    }

    override fun setLocationPermissionGranted(isLocationPermissionGranted: Boolean) {
        TODO("Not yet implemented")
    }
}