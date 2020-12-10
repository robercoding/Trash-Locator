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
import com.rober.trashlocator.data.source.mapsmanager.utils.permissions.PermissionsManagerImpl
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.utils.EspressoIdlingResource
import com.rober.trashlocator.utils.Event
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MapsViewModel @ViewModelInject constructor(
    private val mapsRepositoryImpl: MapsRepositoryImpl,
    private val permissionsManager: PermissionsManagerImpl
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

    private var job: Job? = null

    init {
        Log.i("MapsFragment", "Init viewmodel!")
        _onBackPressed.value = false
        subscribeObservers()
    }

    private var listLocations = mutableListOf<AddressLocation>()
    private var lastNameLocation = ""

    private fun subscribeObservers() {
        mapsRepositoryImpl.addressLocation.observeForever {
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

            _message.value = it
        }
    }

    fun setLastNameLocationEmpty() {
        lastNameLocation = ""
    }

    fun getLastLocation() {
        if (listLocations.size > 1) {
            listLocations.removeLast()

            mapsRepositoryImpl.setUpdateLocationByAddressLocation(listLocations.last(), false)
        } else {
            _onBackPressed.value = true
        }
    }

    private fun isAddressLocationTheSameAsLast(addressLocation: AddressLocation): Boolean {
        if (listLocations.isEmpty()) return false
        return addressLocation == listLocations.last()
    }

    //NEW CHANGES
    //MapsRepository
    fun setGoogleMap(googleMap: GoogleMap) = mapsRepositoryImpl.setGoogleMap(googleMap)
    fun setGoogleMapAndConfiguration(googleMap: GoogleMap) =
        mapsRepositoryImpl.setGoogleMapAndConfiguration(googleMap)

    fun updateLocationUI() = mapsRepositoryImpl.updateLocationUI()
    fun setUpdateLocationByAddressLocation(addressLocation: AddressLocation) {
        if (isAddressLocationTheSameAsLast(addressLocation)) {
            mapsRepositoryImpl.setUpdateLocationByAddressLocation(
                addressLocation,
                false
            ) //To not add the same 2 places in the backstack
        } else {
            mapsRepositoryImpl.setUpdateLocationByAddressLocation(addressLocation, true)
        }
    }

    fun requestLocationUpdate() = mapsRepositoryImpl.requestLocationUpdate()

    //Get list addresses of addresses by name location
    fun getListAddressesByName(nameLocation: String, context: Context) {
        if ((nameLocation == lastNameLocation)) {
            return
        }
        lastNameLocation = nameLocation


        job = viewModelScope.launch { mapsRepositoryImpl.getListAddressesByName(nameLocation) }
        job?.invokeOnCompletion {
            EspressoIdlingResource.decrement()
        }
    }

    fun registerReceiver(broadcastReceiver: BroadcastReceiver) =
        mapsRepositoryImpl.registerReceiver(broadcastReceiver)

    fun unregisterReceiver() = mapsRepositoryImpl.unregisterReceiver()

    fun setLocationPermissionsGranted(isLocationPermissionsGranted: Boolean) =
        permissionsManager.setLocationPermissionGranted(isLocationPermissionsGranted)


    override fun onCleared() {
        super.onCleared()
        Log.i("MapsFragment", "Clear viewmodel")
    }
}