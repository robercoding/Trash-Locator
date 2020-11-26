package com.rober.trashlocator.ui.fragments.maps

import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.GoogleMap
import com.rober.trashlocator.data.repository.maps.MapsRepositoryImpl
import com.rober.trashlocator.data.repository.permissions.PermissionsRepositoryImpl
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.utils.Event
import kotlinx.coroutines.launch

class MapsViewModel @ViewModelInject constructor(
    private val mapsRepositoryImpl: MapsRepositoryImpl,
    private val permissionsRepositoryImpl: PermissionsRepositoryImpl
) : ViewModel() {
    private val TAG = "MapsViewModel"

    //Set list of addresses location for search toolbar
    private val _listAddressesLocation = MutableLiveData<Event<List<AddressLocation>>>()
    val listAddressesLocation: LiveData<Event<List<AddressLocation>>> get() = _listAddressesLocation

    private val _cameraMove = MutableLiveData<Event<Boolean>>()
    val cameraMove: LiveData<Event<Boolean>> = _cameraMove

    private val _onBackPressed = MutableLiveData<Boolean>()
    val onBackPressed: LiveData<Boolean> get() = _onBackPressed

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = _message

    init {
        Log.i("MapsFragment", "Init viewmodel!")
        _onBackPressed.value = false
        subscribeObservers()
    }

    private var listLocations = mutableListOf<AddressLocation>()
    private var lastNameLocation = ""

    private fun subscribeObservers() {
        mapsRepositoryImpl.addressLocation.observeForever {
            Log.i("SeeAddressLocation", "Add addressLOCATION")
            listLocations.add(it)
        }

        mapsRepositoryImpl.listAddressesLocation.observeForever {
            _listAddressesLocation.value = it
        }

        mapsRepositoryImpl.cameraMove.observeForever {
            if (it.hasBeenHandled) return@observeForever
            _cameraMove.value = it
        }

        mapsRepositoryImpl.message.observeForever {
            if (it.hasBeenHandled) return@observeForever

            Log.i("SeeAddressLocation", "Add message")
            _message.value = it
        }
    }

    fun setLastNameLocationEmpty() {
        lastNameLocation = ""
    }

    fun getLastLocation() {
        Log.i("SeeListLocation", "listLocation = ${listLocations.size}")
        if (listLocations.size > 1) {
            listLocations.removeLast()
            Log.i("SeeListLocation", "listLocation removed...= ${listLocations.size}")

            mapsRepositoryImpl.setUpdateLocationByAddressLocation(listLocations.last(), false)
        } else {
            _onBackPressed.value = true
        }
    }

    //NEW CHANGES
    //MapsRepository
    fun setGoogleMap(googleMap: GoogleMap) = mapsRepositoryImpl.setGoogleMap(googleMap)
    fun setGoogleMapAndConfiguration(googleMap: GoogleMap) =
        mapsRepositoryImpl.setGoogleMapAndConfiguration(googleMap)

    fun updateLocationUI() = mapsRepositoryImpl.updateLocationUI()
    fun setUpdateLocationByAddressLocation(addressLocation: AddressLocation) =
        mapsRepositoryImpl.setUpdateLocationByAddressLocation(addressLocation, true)

    fun requestLocationUpdate() = mapsRepositoryImpl.requestLocationUpdate()

    //Get list addresses of addresses by name location
    fun getListAddressesByName(nameLocation: String, context: Context) {
        if ((nameLocation == lastNameLocation)) {
            return
        }
        lastNameLocation = nameLocation

        viewModelScope.launch { mapsRepositoryImpl.getListAddressesByName(nameLocation) }
    }

    fun registerReceiver(broadcastReceiver: BroadcastReceiver) =
        mapsRepositoryImpl.registerReceiver(broadcastReceiver)

    fun unregisterReceiver() = mapsRepositoryImpl.unregisterReceiver()

    //PermissionsRepository
    fun setLocationPermissionsGranted(isLocationPermissionsGranted: Boolean) =
        permissionsRepositoryImpl.setLocationPermissionsGranted(isLocationPermissionsGranted)


    override fun onCleared() {
        super.onCleared()
        Log.i("MapsFragment", "Clear viewmodel")
    }
}