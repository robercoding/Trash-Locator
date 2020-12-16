package com.rober.trashlocator.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
import com.rober.trashlocator.R
import com.rober.trashlocator.databinding.ActivityMapsBinding
import com.rober.trashlocator.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val TAG = "MainActivity"

    private val sharedViewModel: SharedViewModel by viewModels()

    private lateinit var binding: ActivityMapsBinding
    private lateinit var navigationView: NavigationView
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private var drawer: DrawerLayout? = null

    private var isNightMode = false

    override fun attachBaseContext(newBase: Context?) {
//        val lang = "en" // your language or load from SharedPref
        val base = LocaleManager.setLocaleLanguage(newBase)
        super.attachBaseContext(base)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)
        setupView()
    }

    private fun setupView() {
        setupTheme()
        setupNavigationController()
        setupDrawer()
    }

    private fun setupNavigationController() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.containerFragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.graph.startDestination = Destinations.mapsFragment
    }

    private fun setupDrawer() {
        drawer = binding.drawerLayout
        navigationView = binding.navigationView
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer?.addDrawerListener(toggle)
        toggle.syncState()

        //SetNavigationItemSelected let us control fragment recreation by not calling directly to change destination
        navigationView.setNavigationItemSelectedListener(this)

        //NavController recreated fragment and we can't control that from DrawerLayout
//        navigationView.setupWithNavController(navController) //Navigation of drawer now is being listened by navcontroller!
    }

    fun openDrawer() {
        drawer?.openDrawer()
    }

    fun closeDrawer() {
        drawer?.closeDrawer()
    }

    private fun navigateToMapFragment() {
        if (navController.currentDestination?.id == Destinations.mapsFragment) {
            closeDrawer()
            return
        }
        if (!navController.popBackStack(R.id.mapsFragment, false)) {
            navController.navigate(R.id.mapsFragment)
        }
        closeDrawer()
    }

    private fun navigateToTrashStats() {
        if (!navController.popBackStack(R.id.trashStatsFragment, false)) {
            navController.navigate(R.id.trashStatsFragment)
        }
        closeDrawer()
    }

    private fun navigateToContact() {
        if (!navController.popBackStack(R.id.contactFragment, false)) {
            navController.navigate(R.id.contactFragment)
        }
        closeDrawer()
    }

    private fun navigateToAbout() {
        if (!navController.popBackStack(R.id.aboutAppFragment, false)) {
            navController.navigate(R.id.aboutAppFragment)
        }
        closeDrawer()
    }

    private fun navigateToSettings() {
        if (!navController.popBackStack(R.id.settingsFragment, false)) {
            navController.navigate(R.id.settingsFragment)
        }
        closeDrawer()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val destinationId = item.itemId
        if (isDestinationSameAsCurrentDestination(destinationId)) {
            closeDrawer()
            return false
        }

        when (item.itemId) {
            Destinations.mapsFragment -> navigateToMapFragment()
            Destinations.trashStatsFragment -> navigateToTrashStats()
            Destinations.contactFragment -> navigateToContact()
            Destinations.aboutAppFragment -> navigateToAbout()
            Destinations.settingsFragment -> navigateToSettings()
        }
        return false
    }

    private fun isDestinationSameAsCurrentDestination(destinationId: Int): Boolean {
        return destinationId == navController.currentDestination?.id
    }

    override fun onBackPressed() {
        drawer?.let {
            if (it.isDrawerOpen(GravityCompat.START))
                closeDrawer()
            else
                super.onBackPressed()
        }
    }

    private fun setupTheme() {
        val sharedPreferences =
            getSharedPreferences(packageName + "_preferences", Context.MODE_PRIVATE)

        val darkTheme = sharedPreferences.getBoolean(Constants.KEY_SWITCH_THEME, false)
        Log.i("AppTheme", "Is dark theme? ${darkTheme}")
        if (darkTheme) {
            isNightMode = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            isNightMode = false
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun isNightModeSet(): Boolean {
        return isNightMode
    }

    fun findNavController(): NavController {
        return navController
    }

    fun findNavHostFragment(): NavHostFragment {
        return navHostFragment
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "On Pause activity")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "On Stop activity")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "On Destroy activity")

    }

    //set permission in a sharedviewmodel that MapsFragment is listening
    //Asynchronous task waiting for response of the user once launched
    fun requestPermissions(requestPermissions: Array<String>) {
        val test =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.forEach {
                    val permission = it.key
                    when (permission) {
                        Manifest.permission.ACCESS_FINE_LOCATION -> sharedViewModel.registerPermission(
                            Permission.GpsPermission(it.key, it.value)
                        )
                    }
                }
            }

        test.launch(requestPermissions)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.GPS_REQUEST -> sendActivityResultToMapsFragment(requestCode, resultCode)
        }
    }

    private fun sendActivityResultToMapsFragment(requestCode: Int, resultCode: Int) {
        if (isCurrentDestinationMapsFragment())
            navHostFragment.childFragmentManager.fragments[0].onActivityResult(
                requestCode,
                resultCode,
                null
            )
    }

    private fun isCurrentDestinationMapsFragment(): Boolean {
        return navController.currentDestination?.id == Destinations.mapsFragment
    }
}