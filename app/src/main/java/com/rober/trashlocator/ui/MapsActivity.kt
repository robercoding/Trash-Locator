package com.rober.trashlocator.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.navigation.NavigationView
import com.rober.trashlocator.R
import com.rober.trashlocator.databinding.ActivityMapsBinding
import com.rober.trashlocator.utils.Destinations
import com.rober.trashlocator.utils.closeDrawer
import com.rober.trashlocator.utils.openDrawer

class MapsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMapsBinding
    lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var navController: NavController
    private var currentDestinationId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        setupView()
    }

    private fun setupView() {
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
        navController.navigate(Destinations.trashStatsFragment)
        closeDrawer()
    }

    private fun navigateToNotifyErrors() {
        navController.navigate(Destinations.notifyErrorsFragment)
        closeDrawer()
    }

    private fun navigateToAbout() {
        navController.navigate(Destinations.aboutAppFragment)
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
}