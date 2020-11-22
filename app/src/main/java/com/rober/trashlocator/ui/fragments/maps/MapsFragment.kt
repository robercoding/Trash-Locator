package com.rober.trashlocator.ui.fragments.maps

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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
import com.google.maps.android.clustering.ClusterManager
import com.rober.trashlocator.R
import com.rober.trashlocator.databinding.MapsFragmentBinding
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.models.Trash
import com.rober.trashlocator.ui.MapsActivity
import com.rober.trashlocator.ui.base.BaseFragment
import com.rober.trashlocator.ui.base.viewBinding
import com.rober.trashlocator.ui.fragments.maps.utils.IGPSReceiverListener
import com.rober.trashlocator.utils.*
import com.rober.trashlocator.utils.listeners.TextWatcherListener
import com.rober.trashlocator.utils.listeners.interfaces.ICustomLocationListener
import com.rober.trashlocator.utils.listeners.interfaces.RecyclerAddressLocationClickListener
import com.rober.trashlocator.utils.listeners.interfaces.TextListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsFragment : BaseFragment<MapsViewModel>(R.layout.maps_fragment), OnMapReadyCallback,
     TextListener, RecyclerAddressLocationClickListener, IGPSReceiverListener {

    private val TAG = "MapsFragment"

    override val viewModel: MapsViewModel by viewModels()

    private val binding: MapsFragmentBinding by viewBinding(MapsFragmentBinding::bind)

    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private lateinit var clusterManager: ClusterManager<Trash>
    private var cameraPosition: CameraPosition? = null

    private var currentAddressLocation: AddressLocation? = null

    private lateinit var textWatcherListener: TextWatcherListener

    private var isFirstTimeEnter = true
    private var hasBeenDetached = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "On Create")
        if (savedInstanceState != null) {
            isFirstTimeEnter = savedInstanceState[Constants.IS_FIRST_TIME_ENTER] as Boolean
            hasBeenDetached = savedInstanceState[Constants.IS_DETACHED_VALUE] as Boolean
        }
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
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

        if (isFirstTimeEnter) {
            viewModel.setGoogleMapAndConfiguration(googleMap)
            viewModel.updateLocationUI()
        } else if (hasBeenDetached && !isFirstTimeEnter) {
            Log.i("SeeMapsFragment", "On restored")
            viewModel.setGoogleMap(googleMap)
        }

        isFirstTimeEnter = false
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
//            setCluster(listTrash)
        }

        viewModel.listAddressesLocation.observe(viewLifecycleOwner) { listAddressesLocation ->
            if (listAddressesLocation.isNotEmpty()) {
                setSearchAdapter(listAddressesLocation)
            }
        }

        viewModel.addressLocation.observe(viewLifecycleOwner) { eventAddressLocation ->
            if (eventAddressLocation.hasBeenHandled) return@observe
            currentAddressLocation = eventAddressLocation.getContentIfNotHandled()
            currentAddressLocation?.let { moveCamera(it) }
        }

        viewModel.userCameraPosition.observe(viewLifecycleOwner) {
            cameraPosition = it
//            moveCameraByCameraPosition(it)
        }

        viewModel.onBackPressed.observe(viewLifecycleOwner) { onBackPressed ->
            if (!onBackPressed) {
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

    /*
    * We register receivers so Android notifies us of changes in the runtime
    */
    private fun initializeReceivers() {
        viewModel.registerReceiver(GPSBroadcastReceiver(locationManager, this))
    }

    private fun clearFocusSearchToolbar() {
        binding.ETsearchLocation.clearFocus()
        binding.containerToolbar.requestFocus()
        binding.recyclerLocation.hide()
        hideKeyBoard()
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
//        displayToast("Stopped ")
        viewModel.getListAddressesByName(text, requireContext())
    }

    //activated by RecyclerLocationListener
    override fun onAddressLocationClickListener(addressLocation: AddressLocation) {
        //Deactive to don't trigger textWatcher listener to don't trigger onUserStopTyping
        textWatcherListener.isSettingText(true)
        viewModel.setUpdateLocationByAddressLocation(addressLocation)

        binding.ETsearchLocation.setText(addressLocation.streetName)
        clearFocusSearchToolbar()
        textWatcherListener.isSettingText(false)
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
            binding.textLocationSettings.text = message
            binding.textLocationSettings.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green
                )
            )
            binding.textLocationSettings.show()
            viewModel.requestLocationUpdate()
        }
    }

    /*
     * Check for view since this function is called from a runnable that doesn't know about view
     * User could've destroyed the view by changing the theme and this would crash the app
     */
    override fun hideLocationMessage() {
        if (view != null) {
            Log.i(TAG, "Hiding..")
            binding.textLocationSettings.hide()
        }
    }

    //Clean from mapsmanager to view
//    override fun onCameraMoveStarted(p0: Int) {
//        clearFocusSearchToolbar()
//    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.setLocationPermissionsGranted(true)
                    viewModel.updateLocationUI()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "OnSaveInstance!")
        /* OnSaveInstance is called because dark theme is initialized on the start of the application or user changed it by itself
        *  So we check if user already has started using the app or it's because of the initial set theme
        *  Save value if fragment hasBeenDetached to evaluate later on "onMapReady function"
        */
        outState.putBoolean(Constants.IS_FIRST_TIME_ENTER, isFirstTimeEnter)
        val hasBeenDetached = !isAdded
        outState.putBoolean(Constants.IS_DETACHED_VALUE, hasBeenDetached)
//        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "start fragment!")
    }

    override fun onResume() {
        super.onResume()
        initializeReceivers()
    }

    override fun onPause() {
        super.onPause()
        /*
         * If user got dark theme then currentAddressLocation won't be initialized
         * so cameraPosition isn't useful when loading again and setting the position
         */
        viewModel.unregisterReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "OnDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i(TAG, "OnDetach")
    }
}