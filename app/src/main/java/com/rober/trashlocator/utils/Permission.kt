package com.rober.trashlocator.utils

sealed class Permission {
    data class GpsPermission(val key : String, val value : Boolean) : Permission()
}