package com.rober.trashlocator.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.navigation.NavigationView
import com.rober.trashlocator.R
import com.rober.trashlocator.databinding.ActivityMapsBinding
import com.rober.trashlocator.utils.*

class MapsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMapsBinding
    lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var navController: NavController
    var currentDestinationId = -1

    override fun attachBaseContext(newBase: Context?) {
//        val lang = "en" // your language or load from SharedPref
        val base = LocaleManager.setLocaleLanguage(newBase)
        super.attachBaseContext(base)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("SeeActivityCreate", "Oncreate!")
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
        navController = Navigation.findNavController(this, R.id.containerFragment)
        navController.graph.startDestination = Destinations.mapsFragment
        currentDestinationId = Destinations.mapsFragment

        //Abandoned, it recreates fragment with drawer when is in the same fragment
//        navController.addOnDestinationChangedListener { controller, destination, arguments ->
//            when (destination.id) {
//                Destinations.mapsFragment -> navigateToMapFragment()
//            }
//        }
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
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        //SetNavigationItemSelected let us control fragment recreation by not calling directly to change destination
        navigationView.setNavigationItemSelectedListener(this)

        navController.removeOnDestinationChangedListener { controller, destination, arguments ->
            Log.i("SeeActivityCreate", "Someone removed")
        }

        //NavController recreated fragment and we can't control that from DrawerLayout
//        navigationView.setupWithNavController(navController) //Navigation of drawer now is being listened by navcontroller!
    }

    fun openDrawer() {
        if (!this::drawer.isInitialized)
            return

        drawer.openDrawer()
    }

    fun closeDrawer() {
        if (!this::drawer.isInitialized)
            return

        drawer.closeDrawer()
    }

    private fun navigateToMapFragment() {
        navController.navigate(Destinations.mapsFragment)
        closeDrawer()
    }

    private fun navigateToTrashStats() {
        navController.navigate(R.id.action_mapsFragment_to_trashStatsFragment)
        closeDrawer()
    }

    private fun navigateToNotifyErrors() {
        navController.navigate(R.id.action_mapsFragment_to_notifyErrorsFragment)
        closeDrawer()
    }

    private fun navigateToAbout() {
        navController.navigate(R.id.action_mapsFragment_to_aboutAppFragment)
        closeDrawer()
    }

    private fun navigateToSettings() {
        navController.navigate(R.id.action_mapsFragment_to_settingsFragment)
        closeDrawer()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == currentDestinationId) {
            closeDrawer()
            return false
        }

        currentDestinationId = id
        when (item.itemId) {
            Destinations.mapsFragment -> navigateToMapFragment()
            Destinations.trashStatsFragment -> navigateToTrashStats()
            Destinations.notifyErrorsFragment -> navigateToNotifyErrors()
            Destinations.aboutAppFragment -> navigateToAbout()
            Destinations.settingsFragment -> navigateToSettings()
        }
        return false
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setupTheme() {
        val sharedPreferences =
            getSharedPreferences(packageName + "_preferences", Context.MODE_PRIVATE)

        val darkTheme = sharedPreferences.getBoolean(Constants.KEY_SWITCH_THEME, false)
        Log.i("AppTheme", "Is dark theme? ${darkTheme}")
        if (darkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentDestinationId = savedInstanceState.getInt(Constants.CURRENT_DESTINATION)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(Constants.CURRENT_DESTINATION, currentDestinationId)
    }
}