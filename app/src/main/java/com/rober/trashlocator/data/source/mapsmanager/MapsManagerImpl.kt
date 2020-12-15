package com.rober.trashlocator.data.source.mapsmanager

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterManager
import com.rober.trashlocator.R
import com.rober.trashlocator.data.source.mapsmanager.extensionutility.MapsExtensionUtilityManager
import com.rober.trashlocator.data.source.mapsmanager.utils.CustomLocationManager
import com.rober.trashlocator.data.source.mapsmanager.utils.gps.GpsUtils
import com.rober.trashlocator.data.source.mapsmanager.utils.permissions.PermissionsManager
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.models.Trash
import com.rober.trashlocator.utils.CustomClusterRenderer
import com.rober.trashlocator.utils.Event
import com.rober.trashlocator.utils.listeners.CustomLocationListenerImpl
import com.rober.trashlocator.utils.listeners.interfaces.CustomLocationListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MapsManagerImpl constructor(
    private val context: Context,
    private val permissionsManager: PermissionsManager,
    private val gpsUtils: GpsUtils,
    private val mapsExtensionUtilityManager: MapsExtensionUtilityManager,
    private val locationManager: CustomLocationManager
) : GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnCameraMoveStartedListener,
    CustomLocationListener, MapsManager {
    override val TAG = "MapsManager"

    private val _addressLocation = MutableLiveData<AddressLocation>()
    val addressLocation: LiveData<AddressLocation> get() = _addressLocation

    private val _addressesLocation = MutableLiveData<Event<List<AddressLocation>>>()
    val addressesLocation: LiveData<Event<List<AddressLocation>>> = _addressesLocation

    private val _cameraMove = MutableLiveData<Event<Boolean>>()
    val cameraMove: LiveData<Event<Boolean>> = _cameraMove

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> get() = _message

    private var googleMap: GoogleMap? = null
    private lateinit var clusterManager: ClusterManager<Trash>

    private var locationListener: LocationListener? = CustomLocationListenerImpl(this)
    private var receiver: BroadcastReceiver? = null
    private var listTrash = listOf<Trash>()

    override fun setGoogleMap(googleMap: GoogleMap) {
        this.googleMap = googleMap
        setGoogleMapConfiguration()

        clusterManager = ClusterManager(context, googleMap)
        setCluster()

        enableMyLocationButton()
    }

    override fun setGoogleMapAndConfiguration(googleMap: GoogleMap) {
        this.googleMap = googleMap

        clusterManager = ClusterManager(context, googleMap)
        setCluster()
        enableMyLocationButton()
    }

    private fun setGoogleMapConfiguration(){
        this.googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
        this.googleMap?.setOnCameraMoveStartedListener(this)
    }

    override fun updateLocationUI() {
        val isLocationPermissionsOk = permissionsManager.checkLocationPermission()
        if (!isLocationPermissionsOk) {
            permissionsManager.requestLocationPermissions()
            return
        }

        val isGPSEnabled = gpsUtils.isGPSEnabled()
        if (!isGPSEnabled) {
            gpsUtils.requestGPSEnable()
            return
        }

        try {
            if (isLocationPermissionsOk && isGPSEnabled) {
                enableMyLocationButton()
                getDeviceLocation()
            } else {
                permissionsManager.checkLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    override fun setUpdateLocationByAddressLocation(
        addressLocation: AddressLocation,
        addToLiveData: Boolean
    ) {
        if (addToLiveData) {
            _addressLocation.value = addressLocation
        }
        moveCamera(addressLocation)
    }

    //Get list addresses of addresses by name location and set on MutableLiveData
    override suspend fun getListAddressesByName(nameLocation: String) {
        _addressesLocation.value =
            Event(mapsExtensionUtilityManager.getListAddressesByName(nameLocation))
    }

    override fun enableMyLocationButton() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsManager.requestLocationPermissions()
            return
        }
        googleMap?.isMyLocationEnabled = true
        googleMap?.uiSettings?.isMyLocationButtonEnabled = true
        googleMap?.setOnMyLocationButtonClickListener(this)
    }

    private fun getDeviceLocation() {
        val isLocationPermissionsOk = permissionsManager.checkLocationPermission()
        if (!isLocationPermissionsOk) {
            permissionsManager.requestLocationPermissions()
            return
        }

        val isGPSEnabled = gpsUtils.isGPSEnabled()
        if (!isGPSEnabled) {
            Log.i(TAG, "GPS Setting UI to false..")
            gpsUtils.requestGPSEnable()
            return
        }

        try {
            locationListener?.let { locationManager.requestSingleUpdate(it) } ?: _message.postValue(
                Event("Error trying to request a single update")
            )
        } catch (e: Exception) {
            val errorMessage = e.message
            if (errorMessage != null) {
                Log.e(TAG, errorMessage)
            }
        }
    }

    private fun setCluster() {
        //Clear to don't duplicate the cluster that was loaded before
        clusterManager.run {
            googleMap?.clear()
            clearItems()
            cluster()
        }

        val verifiedGoogleMap = googleMap ?: return
        clusterManager = ClusterManager<Trash>(context, googleMap)
        clusterManager.renderer = CustomClusterRenderer(context, googleMap!!, clusterManager)
        //Click listener that zooms the selected cluster trash
        clusterManager.setOnClusterClickListener { clusterTrash ->
            val builder = LatLngBounds.builder()
//            for (trash in clusterTrash.items) {
//                builder.include(trash.position)
//            }
            clusterTrash.items.forEach {
                builder.include(it.position)
            }
            //Animate camera doesn't zoom!
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
//            googleMap.animateCamera(CameraUpdateFactory.zoomTo(100f))

            val bounds = builder.build()

            verifiedGoogleMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    verifiedGoogleMap.cameraPosition.zoom.toInt()
                )
            )

            false
        }

        googleMap?.setOnCameraIdleListener(clusterManager)
        googleMap?.setOnMarkerClickListener(clusterManager)

        clusterManager.addItems(listTrash)
        clusterManager.cluster()
        clusterManager.setAnimation(true)
    }

    private fun moveCamera(addressLocation: AddressLocation) {
        /*
         * Zoom doesnt work!
         * To make animate Camera work we have to add the lapse 2500
         * To don't lag and provide a good UI experience I look for trash after finishing
         */
        var foundDataSet: Boolean
        runBlocking {
            launch(Dispatchers.IO) {
                foundDataSet = mapsExtensionUtilityManager.existsDataSet(addressLocation)
                if (foundDataSet) _message.postValue(Event(context.getString(R.string.dataset_found))) else _message.postValue(
                    Event(context.getString(R.string.dataset_not_found))
                )
            }
        }

        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    addressLocation.location.latitude,
                    addressLocation.location.longitude
                ), 17f
            ), 2000, object : GoogleMap.CancelableCallback {
                override fun onFinish() {
                    setTrashClusterForLocation(addressLocation)
                }

                override fun onCancel() {}
            }
        )
    }

    private fun setTrashClusterForLocation(addressLocation: AddressLocation) {
        runBlocking {
            var listTrashCluster = emptyList<Trash>()
            val job = launch(Dispatchers.IO) {
                listTrashCluster = getTrashCluster(addressLocation)
            }
            job.join()
            if (listTrashCluster.isNotEmpty()) {
                setCluster()
            }
        }
    }

    private suspend fun getTrashCluster(
        addressLocation: AddressLocation
    ): List<Trash> {
        listTrash = googleMap?.let { verifiedGoogleMap ->
            mapsExtensionUtilityManager.getTrashCluster(
                verifiedGoogleMap,
                addressLocation
            )
        } ?: kotlin.run { emptyList() }

        return listTrash
    }

    override fun onCameraMoveStarted(p0: Int) {
        _cameraMove.value = Event(true)
    }

    override fun onMyLocationButtonClick(): Boolean {
        getDeviceLocation()
        return true
    }

    override fun updateCurrentLocation(location: Location) {
        val currentAddressLocation = AddressLocation()
        currentAddressLocation.location = location
        setUpdateLocationByAddressLocation(currentAddressLocation, true)
    }

    override fun requestLocationUpdate() {
        enableMyLocationButton()
        if (addressLocation.value == null) {
            getDeviceLocation()
        }
    }

    override fun registerReceiver(receiver: BroadcastReceiver) {
        this.receiver = receiver
        context.registerReceiver(receiver, IntentFilter("android.location.PROVIDERS_CHANGED"))
    }

    override fun unregisterReceiver() {
        receiver?.let { context.unregisterReceiver(it) }
    }
}