package com.rober.trashlocator.ui.fragments.maps.utils.mapsmanager

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterManager
import com.rober.trashlocator.R
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.models.Trash
import com.rober.trashlocator.ui.fragments.maps.utils.gpsmanager.GPSManager
import com.rober.trashlocator.ui.fragments.maps.utils.mapsmanager.extensionutility.MapsExtensionUtilityManager
import com.rober.trashlocator.ui.fragments.maps.utils.permissions.PermissionsManager
import com.rober.trashlocator.utils.CustomClusterRenderer
import com.rober.trashlocator.utils.Event
import com.rober.trashlocator.utils.listeners.CustomLocationListener
import com.rober.trashlocator.utils.listeners.interfaces.ICustomLocationListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MapsManager constructor(
    private val context: Context,
    private val permissionsManager: PermissionsManager,
    private val gpsManager: GPSManager,
    private val mapsExtensionUtilityManager: MapsExtensionUtilityManager,
    private val locationManager: LocationManager
) : GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnCameraMoveStartedListener,
    ICustomLocationListener {
    private val TAG = "MapsManager"

    private val _addressLocation = MutableLiveData<AddressLocation>()
    val addressLocation: LiveData<AddressLocation> get() = _addressLocation

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> get() = _message

    private var googleMap: GoogleMap? = null
    private lateinit var clusterManager: ClusterManager<Trash>

    private var locationListener: LocationListener? = CustomLocationListener(this)
    private var receiver: BroadcastReceiver? = null

    //    fun setGoogleMapAndLocationListener(googleMap: GoogleMap, customLocationListener: CustomLocationListener){
//        this.googleMap = googleMap
//        locationListener = customLocationListener
//    }
    fun setGoogleMap(googleMap: GoogleMap) {
        this.googleMap = googleMap
        clusterManager = ClusterManager(context, googleMap)
    }

    fun setGoogleMapAndConfiguration(googleMap: GoogleMap) {
        this.googleMap = googleMap
        clusterManager = ClusterManager(context, googleMap)
        this.googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
        this.googleMap?.setOnMyLocationButtonClickListener(this)
        this.googleMap?.setOnCameraMoveStartedListener(this)
    }

    fun updateLocationUI() {
//        if (!isGoogleMapInitialized()) return
        val isLocationPermissionsOk = permissionsManager.checkLocationPermissionAndSettings()
        val isGPSEnabled = gpsManager.checkIfLocationGPSIsEnabled()
        if (!isLocationPermissionsOk) {
            permissionsManager.requestLocationPermissions()
            setMyLocationButton(false)
            return
        }

        if (!isGPSEnabled) {
            gpsManager.requestGPSEnable()
            setMyLocationButton(false)
            return
        }

        try {
            if (isLocationPermissionsOk && isGPSEnabled) {
                setMyLocationButton(true)
                getDeviceLocation()
            } else {
                setMyLocationButton(false)
                permissionsManager.checkLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    fun setUpdateLocationByAddressLocation(
        addressLocation: AddressLocation,
        addToLiveData: Boolean
    ) {
        if (addToLiveData) {
            _addressLocation.value = addressLocation
        }
        moveCamera(addressLocation)
    }

    private fun setMyLocationButton(value: Boolean) {
        if (value) {
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
        } else {
            googleMap?.isMyLocationEnabled = false
            googleMap?.uiSettings?.isMyLocationButtonEnabled = false
        }
    }

    private fun getDeviceLocation() {
        val isLocationPermissionsOk = permissionsManager.checkLocationPermission()
        if (!isLocationPermissionsOk) {
            permissionsManager.requestLocationPermissions()
            setMyLocationButton(false)
            return
        }

        val isGPSEnabled = gpsManager.checkIfLocationGPSIsEnabled()
        if (!isGPSEnabled) {
            Log.i(TAG, "GPS Setting UI to false..")
            gpsManager.requestGPSEnable()
            setMyLocationButton(false)
            return
        }

        try {
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_COARSE
            criteria.powerRequirement = Criteria.POWER_LOW
            criteria.isAltitudeRequired = false
            criteria.isBearingRequired = false
            criteria.isCostAllowed = true
            criteria.horizontalAccuracy = Criteria.ACCURACY_HIGH
            criteria.verticalAccuracy = Criteria.ACCURACY_HIGH

            locationManager.requestSingleUpdate(criteria, locationListener, null)
        } catch (e: Exception) {
            val errorMessage = e.message
            if (errorMessage != null) {
                Log.e(TAG, errorMessage)
            }
        }
    }

    private fun setCluster(listTrash: List<Trash>) {
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

    private fun moveCameraByCameraPosition(cameraPosition: CameraPosition) {
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                cameraPosition.target, cameraPosition.zoom
            )
        )
    }

    private fun setUpdateLocationByLocation(location: Location) {
        val addressLocation = mapsExtensionUtilityManager.getSingleAddressLocation(location)
        _addressLocation.value = addressLocation
        moveCamera(addressLocation)
    }

    private fun moveCamera(addressLocation: AddressLocation) {
        /*
         * Zoom doesnt work!
         * To make animate Camera work we have to add the lapse 2500
         * To don't lag and provide a good UI experience I look for trash after finishing
         */
        var foundDataSet = false
        runBlocking {
            launch(Dispatchers.IO) {
                foundDataSet = mapsExtensionUtilityManager.existsDataSet(addressLocation)

                if (foundDataSet) {
                    _message.postValue(Event(context.getString(R.string.dataset_found)))
                } else {
                    _message.postValue(Event(context.getString(R.string.dataset_not_found)))
                }
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
                    var trashCluster = emptyList<Trash>()
                    runBlocking {
                        val job = launch(Dispatchers.IO) {
                            if (!foundDataSet) return@launch
                            trashCluster = getTrashCluster(addressLocation)
                            if (trashCluster.isEmpty()) {
                                _message.postValue(Event(context.getString(R.string.dataset_found)))
                            }
                        }
                        job.join()
                        if (!foundDataSet || trashCluster.isEmpty()) {
                            return@runBlocking
                        }
                        setCluster(trashCluster)
                    }
                }

                override fun onCancel() {}
            }
        )
    }

    private suspend fun getTrashCluster(
        addressLocation: AddressLocation
    ): List<Trash> {
        return googleMap?.let { verifiedGoogleMap ->
            mapsExtensionUtilityManager.getTrashCluster(
                verifiedGoogleMap,
                addressLocation
            )
        } ?: kotlin.run { emptyList() }
    }

    override fun onCameraMoveStarted(p0: Int) {

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
        setMyLocationButton(true)
        googleMap?.setOnMyLocationButtonClickListener(this)
        if (addressLocation.value == null) {
            getDeviceLocation()
        }
    }

    fun registerReceiver(receiver: BroadcastReceiver) {
        this.receiver = receiver
        context.registerReceiver(receiver, IntentFilter("android.location.PROVIDERS_CHANGED"))
    }

    fun unregisterReceiver() {
        receiver?.let { context.unregisterReceiver(it) }
    }
}