package com.rober.papelerasvalencia.ui.maps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.maps.android.clustering.ClusterManager
import com.rober.papelerasvalencia.MapsActivity
import com.rober.papelerasvalencia.R
import com.rober.papelerasvalencia.databinding.MapsFragmentBinding
import com.rober.papelerasvalencia.models.Trash
import com.rober.papelerasvalencia.ui.base.BaseFragment
import com.rober.papelerasvalencia.ui.base.viewBinding
import com.rober.papelerasvalencia.utils.*
import com.rober.papelerasvalencia.utils.listeners.CustomLocationListener
import com.rober.papelerasvalencia.utils.listeners.TextWatcherListener
import com.rober.papelerasvalencia.utils.listeners.interfaces.TextListener
import com.rober.papelerasvalencia.utils.listeners.interfaces.TrashListener
import org.threeten.bp.Instant
import java.util.*

class MapsFragment : BaseFragment<MapsViewModel>(R.layout.maps_fragment), OnMapReadyCallback,
    TrashListener, TextListener {

    private val TAG = "MapsFragment"

    override val viewModel: MapsViewModel by viewModels()

    private val binding: MapsFragmentBinding by viewBinding(MapsFragmentBinding::bind)

    private var gpsEnabled = false
    private var locationPermissionGranted = false
    private var alreadyRequestLocationPermission = false
    private var gpsBroadcastReceiver: GPSBroadcastReceiver? = null

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private lateinit var clusterManager: ClusterManager<Trash>

    private lateinit var currentLocation: Location
    private var lastTimeLocationRequested: Long = -1

    private lateinit var dialogRequestGps: AlertDialog
    private lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        initializeMaps()
        checkLocationPermission()
        checkIfLocationGPSIsOn()
    }

    private fun initializeMaps() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapsContainer) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.i("MapDeviceLocation", "onMapReady")
        this.googleMap = googleMap
        this.googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        locationListener =
            CustomLocationListener(this)
//
        updateLocationUI()
//        getTrashCluster()
    }

    private fun updateLocationUI() {
        if (!this::googleMap.isInitialized) {
            Log.i(TAG, "Map is not initialized")
            return
        }
        Log.i(TAG, "Map is initialized")


        val isLocationOk = checkLocationPermissionAndSettings()

        if (!isLocationOk) {
            return
        }


        Log.i(TAG, "LocationPermission ${locationPermissionGranted} && gps = ${gpsEnabled}")

        try {
            if (locationPermissionGranted && gpsEnabled) {
                googleMap.isMyLocationEnabled = true
                googleMap.uiSettings?.isMyLocationButtonEnabled = true
                Log.i(TAG, "Go to get device location")
                getDeviceLocation()
            } else {
                Log.i(TAG, "Go to get check location permission")
                googleMap.isMyLocationEnabled = false
                googleMap.uiSettings?.isMyLocationButtonEnabled = false
                checkLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun checkLocationPermissionAndSettings(): Boolean {
        checkLocationPermission()
        if (!locationPermissionGranted && !alreadyRequestLocationPermission) {
            requestLocationPermissions()
            alreadyRequestLocationPermission = true
            return false
        }

        if (!locationPermissionGranted && alreadyRequestLocationPermission) {
            binding.textPermissionApp.text = getString(R.string.location_permission_error)
            binding.textPermissionApp.show()
            binding.textPermissionApp.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )
            return false
        }

        binding.textPermissionApp.hide()
        Log.i(TAG, "location is granted")

        checkIfLocationGPSIsOn()
        if (!gpsEnabled && !dialogRequestGps.isShowing) {
            requestGPSTurnOn()
            return false
        }

        return true
    }

    private fun getDeviceLocation() {
        if (!locationPermissionGranted) {
            checkLocationPermission()
            googleMap.uiSettings?.isMyLocationButtonEnabled = false
            return
        }
        if (!gpsEnabled) {
            checkIfLocationGPSIsOn()
            googleMap.uiSettings?.isMyLocationButtonEnabled = false
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


    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        }
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
        )
    }

    private fun checkIfLocationGPSIsOn() {
        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
        }

        Log.i(TAG, "Checking location and is ${gpsEnabled}")
        if (!gpsEnabled) {
            requestGPSTurnOn()
        }
    }

    private fun requestGPSTurnOn() {
        if (this::dialogRequestGps.isInitialized && dialogRequestGps.isShowing) {
            return
        }

        dialogRequestGps = MaterialAlertDialogBuilder(requireContext())
            .setTitle("GPS is off")
            .setMessage("Do you want to enable GPS, so we display the nearest trash around you?")
            .setPositiveButton("Yes") { dialog, which ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, which ->
                val messageConnectionTV = binding.textLocationSettings
                messageConnectionTV.setBackgroundColor(
                    ContextCompat.getColor(
                        messageConnectionTV.context,
                        R.color.red
                    )
                )
                messageConnectionTV.text = context?.getString(R.string.location_error)
                messageConnectionTV.setTextColor(
                    ContextCompat.getColor(
                        messageConnectionTV.context,
                        R.color.white
                    )
                )
                messageConnectionTV.show()
            }
            .show()
    }


    private fun setCluster(listTrash: List<Trash>) {
        if (this::clusterManager.isInitialized) {
            //Clear to don't duplicate the cluster that was loaded before
            googleMap.clear()
            clusterManager.clearItems()
            clusterManager.cluster()
        }

        clusterManager = ClusterManager<Trash>(context, googleMap)

        clusterManager.renderer = CustomClusterRenderer(requireContext(), googleMap, clusterManager)
        clusterManager.setOnClusterClickListener { clusterTrash ->
            val builder = LatLngBounds.builder()
            for (trash in clusterTrash.items) {
                builder.include(trash.position)
            }
            val bounds = builder.build()

            //Animate camera doesn't zoom!
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
//            googleMap.animateCamera(CameraUpdateFactory.zoomTo(100f))

            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    googleMap.cameraPosition.zoom.toInt()
                )
            )
            false
        }

        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener(clusterManager)

        clusterManager.addItems(listTrash)
        clusterManager.cluster()
        clusterManager.setAnimation(true)
        val locationfake = Location("")
        locationfake.latitude = 28.463674
        locationfake.longitude = -16.251643
    }

    override fun setupListeners() {
        super.setupListeners()

        binding.toolbarSandwich.setOnClickListener {
            (requireActivity() as MapsActivity).openDrawer()
        }

        binding.ETsearchLocation.addTextChangedListener(TextWatcherListener(this))
    }

    private fun subscribeObservers() {
        viewModel.listTrash.observe(viewLifecycleOwner) { listTrash ->
            setCluster(listTrash)
        }

        viewModel.listAddressesLocation.observe(viewLifecycleOwner) { listAddressesLocation ->
            Log.i("SeeListAddresses", "${listAddressesLocation}")
        }
    }

    private fun initializeReceivers() {
        if (gpsBroadcastReceiver == null) {
            gpsBroadcastReceiver =
                GPSBroadcastReceiver(binding.textLocationSettings, locationManager, this)
            requireActivity().registerReceiver(
                gpsBroadcastReceiver,
                IntentFilter("android.location.PROVIDERS_CHANGED")
            )
        }
    }

    override fun onUserStopTyping(text: String) {
        viewModel.getAdressesByName(text, requireContext())
    }

    override fun updateCurrentLocation(location: Location) {
        val currentLocationModified = Location("")
        currentLocationModified.longitude = -16.251763
        currentLocationModified.latitude = 28.463636
        currentLocation = currentLocationModified
        if (!this::googleMap.isInitialized)
            return

        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    currentLocation.latitude,
                    currentLocation.longitude
                ), 17f
            )
        )

        viewModel.getTrashCluster(googleMap, currentLocation, requireContext())
    }

    override fun requestLocationUpdate() {
        if (!Utils.canUserRequestUpdateLocation(lastTimeLocationRequested)) {
            return
        }

        lastTimeLocationRequested = Instant.now().epochSecond
        getDeviceLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                    updateLocationUI()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!this::clusterManager.isInitialized) {
            updateLocationUI()
        }
        checkLocationPermissionAndSettings()
        initializeReceivers()
    }
}