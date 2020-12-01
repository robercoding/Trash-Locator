package com.rober.trashlocator.ui.fragments.maps

import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.rober.trashlocator.R
import com.rober.trashlocator.databinding.MapsFragmentBinding
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.ui.MapsActivity
import com.rober.trashlocator.ui.base.BaseFragment
import com.rober.trashlocator.ui.base.viewBinding
import com.rober.trashlocator.data.source.mapsmanager.utils.IGPSReceiverListener
import com.rober.trashlocator.utils.Constants
import com.rober.trashlocator.utils.GPSBroadcastReceiver
import com.rober.trashlocator.utils.hide
import com.rober.trashlocator.utils.listeners.TextWatcherListener
import com.rober.trashlocator.utils.listeners.interfaces.RecyclerAddressLocationClickListener
import com.rober.trashlocator.utils.listeners.interfaces.TextListener
import com.rober.trashlocator.utils.show
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MapsFragment : BaseFragment<MapsViewModel>(R.layout.maps_fragment), OnMapReadyCallback,
    TextListener, RecyclerAddressLocationClickListener, IGPSReceiverListener {

    private val TAG = "MapsFragment"

    override val viewModel: MapsViewModel by viewModels()
    private val binding: MapsFragmentBinding by viewBinding(MapsFragmentBinding::bind)

    @Inject
    lateinit var locationManager: LocationManager

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

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
        viewModel.listAddressesLocation.observe(viewLifecycleOwner) { eventListAddressesLocation ->
            setSearchAdapter(eventListAddressesLocation.getContentIfNotHandled() ?: return@observe)
        }

        viewModel.cameraMove.observe(viewLifecycleOwner) {
            if (it.hasBeenHandled) return@observe
            clearFocusSearchToolbar()
        }

        viewModel.onBackPressed.observe(viewLifecycleOwner) { onBackPressed ->
            if (!onBackPressed) {
                return@observe
            }

            defaultOnBackPressed()
        }
        viewModel.message.observe(viewLifecycleOwner) { eventMessage ->
            displayToast(eventMessage.getContentIfNotHandled() ?: return@observe)
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

    //This function is executed in background thread from TextListener, so we need main thread to update UI
    override fun onUserStopTypingIsEmpty() {
        requireActivity().runOnUiThread { setSearchAdapter(emptyList()) }
        viewModel.setLastNameLocationEmpty()
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