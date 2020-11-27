package com.rober.trashlocator.data.repository.permissions

interface IPermissionsRepository {
    fun setLocationPermissionsGranted(isLocationPermissionsGranted: Boolean)
}