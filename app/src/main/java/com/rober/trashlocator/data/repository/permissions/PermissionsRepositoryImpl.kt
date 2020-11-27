package com.rober.trashlocator.data.repository.permissions

import com.rober.trashlocator.ui.fragments.maps.utils.permissions.PermissionsManager

class PermissionsRepositoryImpl constructor(
    private val permissionsManager: PermissionsManager
) : IPermissionsRepository {
    override fun setLocationPermissionsGranted(isLocationPermissionsGranted: Boolean) =
        permissionsManager.setLocationPermissionGranted(isLocationPermissionsGranted)
}