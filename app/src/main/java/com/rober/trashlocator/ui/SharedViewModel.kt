package com.rober.trashlocator.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rober.trashlocator.utils.Event
import com.rober.trashlocator.utils.Permission

class SharedViewModel : ViewModel() {
    private val _requestPermission = MutableLiveData<Event<Permission>>()
    val requestPermission: LiveData<Event<Permission>>
        get() = _requestPermission

    fun registerPermission(permission: Permission) {
        _requestPermission.value = Event(permission)
    }
}