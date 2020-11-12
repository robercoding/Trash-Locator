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
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
import com.rober.trashlocator.R
import com.rober.trashlocator.databinding.ActivityMapsBinding
import com.rober.trashlocator.utils.*

class MapsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMapsBinding
    lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController
    var currentDestinationId = -1

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
        currentDestinationId = Destinations.mapsFragment
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

    private fun navigateToNotifyErrors() {
        if (!navController.popBackStack(R.id.notifyErrorsFragment, false)) {
            navController.navigate(R.id.notifyErrorsFragment)
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
            closeDrawer()
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

    override fun onPause() {
        Log.i(TAG, "On Pause activity")
        super.onPause()
    }

    override fun onStop() {
        Log.i(TAG, "On Stop activity")
        super.onStop()
    }

    override fun onDestroy() {
        Log.i(TAG, "On Pause activity")
        super.onDestroy()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.i(TAG, "RestoreInstance")
        currentDestinationId = savedInstanceState.getInt(Constants.CURRENT_DESTINATION)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "SaveInstance")
        outState.putInt(Constants.CURRENT_DESTINATION, currentDestinationId)
    }
}