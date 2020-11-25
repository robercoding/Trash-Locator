package com.rober.trashlocator.ui.fragments.maps

import android.content.BroadcastReceiver
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.rober.trashlocator.R
import com.rober.trashlocator.data.repository.maps.MapsRepositoryImpl
import com.rober.trashlocator.data.repository.permissions.PermissionsRepositoryImpl
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.models.Trash
import com.rober.trashlocator.models.TrashLocation
import com.rober.trashlocator.utils.Event
import com.rober.trashlocator.utils.LocalitiesDataset
import com.rober.trashlocator.utils.Utils
import com.rober.trashlocator.utils.getStringResources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsViewModel @ViewModelInject constructor(
    private val mapsRepositoryImpl: MapsRepositoryImpl,
    private val permissionsRepositoryImpl: PermissionsRepositoryImpl
) : ViewModel() {
    private val TAG ="MapsViewModel"

    lateinit var geoCoder: Geocoder

    //Set list of addresses location for search toolbar
    private val _listAddressesLocation = MutableLiveData<List<AddressLocation>>()
    val listAddressesLocation: LiveData<List<AddressLocation>> get() = _listAddressesLocation

    private val _cameraMove = MutableLiveData<Event<Boolean>>()
    val cameraMove : LiveData<Event<Boolean>> = _cameraMove

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

    private fun subscribeObservers(){
        mapsRepositoryImpl.addressLocation.observeForever{
            Log.i("SeeAddressLocation", "Add addressLOCATION")
            listLocations.add(it)
        }

        mapsRepositoryImpl.cameraMove.observeForever{
            if(it.hasBeenHandled) return@observeForever
            _cameraMove.value = it
        }

        mapsRepositoryImpl.message.observeForever{
            if(it.hasBeenHandled) return@observeForever

            Log.i("SeeAddressLocation", "Add message")
            _message.value = it
        }
    }

    //Get list addresses of addresses by name location and set on MutableLiveData
    fun getListAddressesByName(nameLocation: String, context: Context) {
        if ((nameLocation == lastNameLocation)) {
            return
        }
        lastNameLocation = nameLocation

        geoCoder = Geocoder(context)

        val addresses = geoCoder.getFromLocationName(nameLocation, 5)
        val mutableListAddressesLocation = mutableListOf<AddressLocation>()
        for (address in addresses) {
            val location = AddressLocation()

            val addressLine = address.getAddressLine(0)

            location.streetName = addressLine
            location.location.latitude = address.latitude
            location.location.longitude = address.longitude
            location.localityName = if (address.locality == null) "" else address.locality
            location.localityAdminAreaName =
                if (address.adminArea == null) "" else address.adminArea

            mutableListAddressesLocation.add(location)
        }

        _listAddressesLocation.postValue(mutableListAddressesLocation.toList())
    }

    fun setLastNameLocationEmpty(){
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

    override fun onCleared() {
        super.onCleared()
        Log.i("MapsFragment", "Clear viewmodel")
    }

    //NEW CHANGES
    //MapsRepository
    fun setGoogleMap(googleMap: GoogleMap) = mapsRepositoryImpl.setGoogleMap(googleMap)
    fun setGoogleMapAndConfiguration(googleMap: GoogleMap) = mapsRepositoryImpl.setGoogleMapAndConfiguration(googleMap)

    fun updateLocationUI() = mapsRepositoryImpl.updateLocationUI()
    fun setUpdateLocationByAddressLocation(addressLocation: AddressLocation) = mapsRepositoryImpl.setUpdateLocationByAddressLocation(addressLocation, true)
    fun requestLocationUpdate() = mapsRepositoryImpl.requestLocationUpdate()

    fun registerReceiver(broadcastReceiver: BroadcastReceiver) = mapsRepositoryImpl.registerReceiver(broadcastReceiver)
    fun unregisterReceiver() = mapsRepositoryImpl.unregisterReceiver()

    //PermissionsRepository
    fun setLocationPermissionsGranted(isLocationPermissionsGranted : Boolean) = permissionsRepositoryImpl.setLocationPermissionsGranted(isLocationPermissionsGranted)
}