package com.rober.trashlocator.ui.fragments.maps

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.maps.android.clustering.ClusterManager
import com.rober.trashlocator.R
import com.rober.trashlocator.databinding.MapsFragmentBinding
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.models.Trash
import com.rober.trashlocator.ui.MapsActivity
import com.rober.trashlocator.ui.base.BaseFragment
import com.rober.trashlocator.ui.base.viewBinding
import com.rober.trashlocator.utils.*
import com.rober.trashlocator.utils.listeners.CustomLocationListener
import com.rober.trashlocator.utils.listeners.TextWatcherListener
import com.rober.trashlocator.utils.listeners.interfaces.ICustomLocationListener
import com.rober.trashlocator.utils.listeners.interfaces.RecyclerAddressLocationClickListener
import com.rober.trashlocator.utils.listeners.interfaces.TextListener
import org.threeten.bp.Instant

class MapsFragment : BaseFragment<MapsViewModel>(R.layout.maps_fragment), OnMapReadyCallback,
    ICustomLocationListener, TextListener, RecyclerAddressLocationClickListener,
    GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnMyLocationButtonClickListener {

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
    private var cameraPosition: CameraPosition? = null

    private var currentAddressLocation: AddressLocation? = null
    private var lastTimeLocationRequested: Long = -1

    private lateinit var dialogRequestGps: AlertDialog
    private lateinit var textWatcherListener: TextWatcherListener

    private var hasBeenDetached = false
    private var onRestored = false
    private var listSavedTrash =
        listOf<Trash>() //Only useful when change rotation and restore trash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "On Create")
        if (savedInstanceState != null) {
            onRestored = true
            currentAddressLocation =
                savedInstanceState[Constants.CURRENT_ADDRESS_LOCATION] as AddressLocation?
            cameraPosition =
                savedInstanceState[Constants.GOOGLE_MAP_CAMERA_POSITION] as CameraPosition?

            hasBeenDetached = savedInstanceState[Constants.IS_DETACHED_VALUE] as Boolean
        }
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        initializeMaps()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return super.onCreateView(inflater, container, savedInstanceState)
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
        Log.i("SeeMapsFragment", "OnMapReady")
        this.googleMap = googleMap
        this.googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.setOnMyLocationButtonClickListener(this)

        this.googleMap.setOnCameraMoveStartedListener(this)
        locationListener =
            CustomLocationListener(this)

        val tempCameraPosition = cameraPosition
        if (!hasBeenDetached && onRestored && tempCameraPosition != null) {
            Log.i("SeeMapsFragment", "Has been detached")
            setMyLocationButton(true)
            moveCameraByCameraPosition(tempCameraPosition)
            setCluster(listSavedTrash)
        } else if (tempCameraPosition != null) {
            Log.i("SeeMapsFragment", "Temp camera position")
            moveCameraByCameraPosition(tempCameraPosition)
            setCluster(listSavedTrash)
        } else if (currentAddressLocation != null) {
            Log.i("SeeMapsFragment", "current address")
            val tempCurrentAddressLocation = currentAddressLocation!!
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        tempCurrentAddressLocation.location.latitude,
                        tempCurrentAddressLocation.location.longitude
                    ), 17f
                )
            )
        } else {
            Log.i("SeeMapsFragment", "UpdateLocation")
            updateLocationUI()
        }
    }

    private fun updateLocationUI() {
        if (!isGoogleMapInitialized()) return

        val isLocationOk = checkLocationPermissionAndSettings()
        if (!isLocationOk) return

        Log.i(TAG, "LocationPermission ${locationPermissionGranted} && gps = ${gpsEnabled}")

        try {
            if (locationPermissionGranted && gpsEnabled) {
                setMyLocationButton(true)
                getDeviceLocation()
            } else {
                setMyLocationButton(false)
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
        if (!gpsEnabled) {
            requestGPSTurnOn()
            return false
        }

        return true
    }

    private fun getDeviceLocation() {
        checkLocationPermission()
        if (!locationPermissionGranted) {
            Log.i(TAG, "Location Setting UI to false..")
            requestLocationPermissions()
            googleMap.uiSettings?.isMyLocationButtonEnabled = false
            return
        }

        checkIfLocationGPSIsOn()
        if (!gpsEnabled) {
            Log.i(TAG, "GPS Setting UI to false..")
            requestGPSTurnOn()
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
            e.message?.let {
                Log.e(TAG, it)
            }
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
    }

    private fun moveCamera(addressLocation: AddressLocation) {
        /*
         * Zoom doesnt work!
         * To make animate Camera work we have to add the lapse 2500
         * To don't lag and provide a good UI experience I look for trash after finishing
         */
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    addressLocation.location.latitude,
                    addressLocation.location.longitude
                ), 17f
            ), 2000, object : GoogleMap.CancelableCallback {
                override fun onFinish() {
                    viewModel.getTrashCluster(googleMap, addressLocation, requireContext())
                }

                override fun onCancel() {}
            }
        )
    }

    private fun moveCameraByCameraPosition(cameraPosition: CameraPosition) {
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                cameraPosition.target, cameraPosition.zoom
            )
        )
    }

    private fun setSearchAdapter(listAddressLocation: List<AddressLocation>) {
        val searchAdapter = SearchLocationAdapter(listAddressLocation, this)

        binding.recyclerLocation.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.HORIZONTAL
                )
            )
            scheduleLayoutAnimation()
        }

        binding.recyclerLocation.show()
    }

    private fun subscribeObservers() {
        viewModel.listTrash.observe(viewLifecycleOwner) { listTrash ->
            //If not initialized, this means is restoring so we get the latest listTrash
            Log.i("SeeListTrash", "List trash load")
            if (!isGoogleMapInitialized()) {
                listSavedTrash = listTrash
                return@observe
            }
            setCluster(listTrash)
        }

        viewModel.listAddressesLocation.observe(viewLifecycleOwner) { listAddressesLocation ->
            if (listAddressesLocation.isNotEmpty()) {
                if (!isGoogleMapInitialized()) return@observe
                setSearchAdapter(listAddressesLocation)
            }
        }

        viewModel.addressLocation.observe(viewLifecycleOwner) { eventAddressLocation ->
            if (eventAddressLocation.hasBeenHandled) return@observe
            currentAddressLocation = eventAddressLocation.getContentIfNotHandled()
            if (!isGoogleMapInitialized()) return@observe
            currentAddressLocation?.let { moveCamera(it) }
        }

        viewModel.userCameraPosition.observe(viewLifecycleOwner) {
            cameraPosition = it
            Log.i("SeeMapsFragment", "Observe camera position")
//            moveCameraByCameraPosition(it)
        }

        viewModel.onBackPressed.observe(viewLifecycleOwner) { onBackPressed ->
            if (!onBackPressed || !isGoogleMapInitialized()) {
                return@observe
            }

            defaultOnBackPressed()
        }

        viewModel.message.observe(viewLifecycleOwner) { eventMessage ->
            eventMessage.getContentIfNotHandled()?.let {
                displayToast(it)
            }
        }
    }

    private fun initializeReceivers() {
        if (gpsBroadcastReceiver == null) {
            gpsBroadcastReceiver =
                GPSBroadcastReceiver(locationManager, this)
            requireActivity().registerReceiver(
                gpsBroadcastReceiver,
                IntentFilter("android.location.PROVIDERS_CHANGED")
            )
        }
    }

    private fun isGoogleMapInitialized(): Boolean {
        if (!this::googleMap.isInitialized) {
            return false
        }
        return true
    }

    private fun clearFocusSearchToolbar() {
        binding.ETsearchLocation.clearFocus()
        binding.containerToolbar.requestFocus()
        binding.recyclerLocation.hide()
        hideKeyBoard()
    }

    private fun setMyLocationButton(value: Boolean) {
        if (!this::googleMap.isInitialized) return

        if (value) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestLocationPermissions()
                return
            }

            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.setOnMyLocationButtonClickListener(this)
        } else {
            googleMap.isMyLocationEnabled = false
            googleMap.uiSettings.isMyLocationButtonEnabled = false
        }

    }

    override fun setupListeners() {
        super.setupListeners()

        binding.toolbarSandwich.setOnClickListener {
            (requireActivity() as MapsActivity).openDrawer()
        }

        textWatcherListener = TextWatcherListener(this)
        binding.ETsearchLocation.addTextChangedListener(textWatcherListener)
        binding.ETsearchLocation.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.recyclerLocation.show()
            }
        }
    }

    override fun detectOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.getLastLocation()
                }
            })
    }

    override fun onUserStopTyping(text: String) {
        viewModel.getListAddressesByName(text, requireContext())
    }

    //activated by RecyclerLocationListener
    override fun onAddressLocationClickListener(addressLocation: AddressLocation) {
        //Deactive to don't trigger textWatcher listener to don't trigger onUserStopTyping
        textWatcherListener.setIsSettingText(true)
        viewModel.setUpdateLocationByAddressLocation(addressLocation)

        binding.ETsearchLocation.setText(addressLocation.streetName)
        textWatcherListener.setIsSettingText(false)
    }

    //activated by a SingleRequestLocation
    override fun updateCurrentLocation(location: Location) {
        Log.i("LocationTrack", "SingleRequest!")
        //Location modified for testing, emulator points out to google headquarters
        //val currentAddressLocationModified = Location("")
        //currentAddressLocationModified.longitude = -16.251763
        //currentAddressLocationModified.latitude = 28.463636

        //Everytime we get their device location we save currentAdressLocation
        currentAddressLocation = AddressLocation()
        currentAddressLocation?.location = location
        if (!isAdded) {
            hasBeenDetached = true
            return
        }

        viewModel.setUpdateLocationByLocation(location, requireContext())
    }

    //Activated when location goes back!
    @SuppressLint("MissingPermission")
    override fun requestLocationUpdate() {
        if (!Utils.canUserRequestUpdateLocation(lastTimeLocationRequested)) {
            return
        }
        Log.i("LocationTrack", "SingleRequest!")

        setMyLocationButton(true)
        googleMap.setOnMyLocationButtonClickListener(this)
        if (currentAddressLocation == null) {
            Log.i("LocationTrack", "Null")
            lastTimeLocationRequested = Instant.now().epochSecond
            getDeviceLocation()
        }
    }

    override fun showLocationMessage(message: String, error: Boolean) {
        if (error) {
            binding.textLocationSettings.text = message
            binding.textLocationSettings.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )
            binding.textLocationSettings.show()
        } else {
            setMyLocationButton(true)
            binding.textLocationSettings.text = message
            binding.textLocationSettings.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green
                )
            )
            binding.textLocationSettings.show()
        }
    }

    override fun hideLocationMessage() {
        binding.textLocationSettings.hide()
    }

    override fun onCameraMoveStarted(p0: Int) {
        clearFocusSearchToolbar()
    }

    override fun onMyLocationButtonClick(): Boolean {
        getDeviceLocation()
        return true
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
                    if (currentAddressLocation == null) {
                        Log.i("UpdateLocationUi", "on request")
                        updateLocationUI()
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "OnSaveInstance!")
        if (currentAddressLocation != null) {
            outState.putParcelable(Constants.CURRENT_ADDRESS_LOCATION, currentAddressLocation)
        }
        /*
         * Set currentaddresslocation != null because when dark theme is initialized on the start of the application
         * If is null it doesn't make sense to get camera position because it's the position by default
         * if isn't null it's ok, user has already done something in map
         */
        if (this::googleMap.isInitialized && currentAddressLocation != null) {
            outState.putParcelable(Constants.GOOGLE_MAP_CAMERA_POSITION, googleMap.cameraPosition)
        }
        outState.putBoolean(Constants.IS_DETACHED_VALUE, isAdded)
    }


    override fun onStart() {
        super.onStart()
        Log.i(TAG, "start fragment!")
    }

    override fun onResume() {
        super.onResume()
        if (!this::clusterManager.isInitialized) {
            if (currentAddressLocation == null) {
                updateLocationUI()
            }
        }
        checkLocationPermissionAndSettings()
        initializeReceivers()
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "Pause fragment!")
        /*
         * If user got dark theme then currentAddressLocation won't be initialized
         * so cameraPosition isn't useful when loading again and setting the position
         */
        if (currentAddressLocation != null) {
            viewModel.setUserCameraPosition(googleMap.cameraPosition)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Destroy fragment!")
    }
}