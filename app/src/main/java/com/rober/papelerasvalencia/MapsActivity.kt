package com.rober.papelerasvalencia

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.*
import android.location.LocationListener
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.rober.papelerasvalencia.listeners.CustomLocationListener
import com.rober.papelerasvalencia.models.Trash
import com.rober.papelerasvalencia.listeners.TrashListener
import com.rober.papelerasvalencia.utils.GPSBroadcastReceiver


class MapsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, TrashListener {

    private val TAG = "MainActivity"

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var mMap: GoogleMap

    private var gpsEnabled = false
    private var locationPermissionGranted = false

    private lateinit var dialogRequestGps: AlertDialog

    private var lastKnownLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()
        checkIfLocationGPSIsOn()
        setupListeners()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.i("MapDeviceLocation", "onMapReady")
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        locationListener =
            CustomLocationListener(window.decorView.findViewById(R.id.map), mMap, this)

        updateLocationUI()
//        getTrashCluster()
    }

    private fun getTrashCluster() {
        val layer = GeoJsonLayer(mMap, R.raw.papeleras_canarias, baseContext)

        val places = mutableListOf<Trash>()
        val listDistances = mutableListOf<Float>()
//        Log.i("MapDeviceLocation", "${layer.features}")
        for (feature in layer.features) {
//            val featureSantaCruz = feature as FeaturesSantaCruz
//            val featureSantaCruz = feature

//            val properties = feature.properties
//            val properties = feature.properties

//            val place = MyItem(properties.gradY, properties.gradX, "${properties.direction}", "")
//            val place = MyItem(properties.gradY, properties.gradX, "${properties.direction}", "")


            //            val properties = feature.properties as PropertiesX

            Log.i("MapDeviceLocation", "Calculate")

            val latLng = feature.geometry.geometryObject as LatLng
            val locationPlace = Location("")
            locationPlace.latitude = latLng.latitude
            locationPlace.longitude = latLng.longitude

            val distance = currentLocation.distanceTo(locationPlace)
            Log.i("MapDeviceLocation", "${distance}")


//            listDistances.add(distance)
            if (distance < 100f) {
                val place = Trash(latLng.latitude, latLng.longitude, "Cluster?", "${distance}m")

                places.add(place)
            }
        }
//            layer.addLayerToMap()
        val clusterManager = ClusterManager<Trash>(this, mMap)

        clusterManager.renderer = CustomClusterRenderer(this, mMap, clusterManager)


        mMap.setOnCameraIdleListener(clusterManager)
        mMap.setOnMarkerClickListener(clusterManager)

        Log.i("MapDeviceLocation", "Places = ${places.size}")
        clusterManager.addItems(places)
        clusterManager.setAnimation(true)
    }

    private fun updateLocationUI() {
        if (!this::mMap.isInitialized) {
            Log.i(TAG, "Map is not initialized")
            return
        }
        Log.i(TAG, "Map is initialized")


        if (!locationPermissionGranted) {
            checkLocationPermission()
            return
        }
        Log.i(TAG, "location is granted")

        checkIfLocationGPSIsOn()
        if (!gpsEnabled && !dialogRequestGps.isShowing) {
            requestGPSTurnOn()
            return
        }
        Log.i(TAG, "gps is enabled")

        try {
            if (locationPermissionGranted && gpsEnabled) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings?.isMyLocationButtonEnabled = true
                Log.i(TAG, "Go to get device location")
                getDeviceLocation()
            } else {
                Log.i(TAG, "Go to get check location permission")
                mMap.isMyLocationEnabled = false
                mMap.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                checkLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getDeviceLocation() {
        if (!locationPermissionGranted) {
            checkLocationPermission()
            mMap.uiSettings?.isMyLocationButtonEnabled = false
            return
        }
        if (!gpsEnabled) {
            checkIfLocationGPSIsOn()
            mMap.uiSettings?.isMyLocationButtonEnabled = false
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

            val locationResult = fusedLocationProviderClient.lastLocation
//            locationResult.addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    lastKnownLocation = task.result
//
//                    if (lastKnownLocation == null) {
//                        Log.i(TAG, "lastKnownLocation null")
//                        requestLocationUpdateCallback()
//                        return@addOnCompleteListener
//                    }
//
//                    Log.i(TAG, "Fake")
////                    currentLocation = lastKnownLocation!!
//                    //Fake location
//                    val currentLocationModified = Location("")
//                    currentLocationModified.longitude = -16.251763
//                    currentLocationModified.latitude = 28.463636
//                    currentLocation = currentLocationModified
//
//                    mMap.moveCamera(
//                        CameraUpdateFactory.newLatLngZoom(
//                            LatLng(
//                                currentLocation.latitude,
//                                currentLocation.longitude
//                            ), 20.toFloat()
//                        )
//                    )
//
//                    Log.i("SeeLocation", "if")
//                    getTrashCluster()
//
//                } else {
//                    val message = task.exception?.message
//
//                    if (message == null) {
//                        displayToast("We are having issues to retrieve your actual location, sorry!")
//                        return@addOnCompleteListener
//                    }
//                    displayToast(message)
//                }
//            }
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }
    }


    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    private fun checkIfLocationGPSIsOn() {
        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
        }

        if (!gpsEnabled) {
            requestGPSTurnOn()
        }
    }

    private fun requestGPSTurnOn() {
//        Log.i(TAG, "gps request")
//        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//        startActivity(intent)
        if (this::dialogRequestGps.isInitialized && dialogRequestGps.isShowing) {
            return
        }

        dialogRequestGps = MaterialAlertDialogBuilder(this)
            .setTitle("GPS is off")
            .setMessage("Do you want to enable GPS, so we display the nearest trash around you?")
            .setPositiveButton("Yes") { dialog, which ->
                startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                dialog.dismiss()
            }
            .setNegativeButton("No"){ dialog, which ->
                val messageConnectionTV = findViewById<TextView>(R.id.messageConnection)
                messageConnectionTV.setBackgroundColor(ContextCompat.getColor(messageConnectionTV.context, R.color.red))
                messageConnectionTV.text = "GPS is disconnected :("
                messageConnectionTV.setTextColor(ContextCompat.getColor(messageConnectionTV.context, R.color.white))
                messageConnectionTV.visibility = View.VISIBLE
            }
            .show()
    }

    private fun requestSingleUpdateLocationManager(){
//        locationListener
    }

    private fun requestLocationUpdateCallback() {
        val locationRequest = LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(1000)
            .setFastestInterval(1000)
            .setNumUpdates(1)

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                val location = p0?.lastLocation
                if (location != null) {
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(location.latitude, location.longitude),
                            11.toFloat()
                        )
                    )
                } else {
                    Toast.makeText(
                        applicationContext,
                        "We are having issues to get your actual location",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        if (!locationPermissionGranted) {
            checkLocationPermission()
            return
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private fun displayToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    private fun setupView(){
        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        val toggle = ActionBarDrawerToggle(this, drawer, findViewById(R.id.appBarLayout), R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun setupListeners() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val gpsBroadcastReceiver = GPSBroadcastReceiver(findViewById(R.id.messageConnection), locationManager)
        registerReceiver(gpsBroadcastReceiver, IntentFilter("android.location.PROVIDERS_CHANGED"))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                    updateLocationUI()
                }
            }
        }
    }

    override fun updateCurrentLocation(location: Location) {
        currentLocation = location
        getTrashCluster()
    }

    override fun getTrashAround() {
    }

    override fun getTrashSantaCruz() {
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        updateLocationUI()
    }

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0
    }
}