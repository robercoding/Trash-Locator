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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.maps.android.clustering.ClusterManager
import com.rober.papelerasvalencia.R
import com.rober.papelerasvalencia.databinding.MapsFragmentBinding
import com.rober.papelerasvalencia.listeners.CustomLocationListener
import com.rober.papelerasvalencia.listeners.TrashListener
import com.rober.papelerasvalencia.models.Trash
import com.rober.papelerasvalencia.ui.base.BaseFragment
import com.rober.papelerasvalencia.ui.base.viewBinding
import com.rober.papelerasvalencia.utils.*

class MapsFragment : BaseFragment<MapsViewModel>(R.layout.maps_fragment), OnMapReadyCallback,
    TrashListener {

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

    private lateinit var dialogRequestGps: AlertDialog

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(MapsViewModel::class.java)
        subscribeObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            CustomLocationListener(googleMap, this)
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


        val isPermissionsOk = checkGeneralPermissions()

        if(!isPermissionsOk){
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

    private fun checkGeneralPermissions(): Boolean{
        checkLocationPermission()
        if (!locationPermissionGranted && !alreadyRequestLocationPermission) {
            requestLocationPermissions()
            alreadyRequestLocationPermission = true
            return false
        }

        if (!locationPermissionGranted && alreadyRequestLocationPermission) {
            binding.textPermissionApp.text =
                "We don't have permissions to get your current location, so we can't help to find nearest trash around you :("
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
            Log.e(TAG, e.message)
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
                messageConnectionTV.text = "GPS is disconnected :("
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

    override fun setupListeners() {
        super.setupListeners()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }

    private fun subscribeObservers() {
        viewModel.listTrash.observe(viewLifecycleOwner, Observer { listTrash ->
            setCluster(listTrash)
        })
    }

    private fun setCluster(listTrash: List<Trash>) {
        if(this::clusterManager.isInitialized){
            googleMap.clear()
            clusterManager.clearItems()
        }

        clusterManager = ClusterManager<Trash>(context, googleMap)

        clusterManager.renderer = CustomClusterRenderer(requireContext(), googleMap, clusterManager)


        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener(clusterManager)

//        Log.i("MapDeviceLocation", "Places = ${places.size}")
        clusterManager.addItems(listTrash)
        clusterManager.setAnimation(true)
    }

    private fun initializeReceivers() {
        if (gpsBroadcastReceiver == null) {
            gpsBroadcastReceiver =
                GPSBroadcastReceiver(binding.textLocationSettings, locationManager)
            requireActivity().registerReceiver(
                gpsBroadcastReceiver,
                IntentFilter("android.location.PROVIDERS_CHANGED")
            )
        }
    }

    override fun updateCurrentLocation(location: Location) {
        currentLocation = location
        if(!this::googleMap.isInitialized)
            return

        viewModel.getTrashCluster(googleMap, currentLocation, requireContext())
    }

    override fun getTrashAround() {
    }

    override fun getTrashSantaCruz() {
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
        if(!this::clusterManager.isInitialized){
            updateLocationUI()
        }
        checkGeneralPermissions()
        initializeReceivers()
    }
}