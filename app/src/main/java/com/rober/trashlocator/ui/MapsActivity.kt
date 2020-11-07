package com.rober.trashlocator.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.get
import androidx.navigation.ui.setupWithNavController
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
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            Log.i("SeeNavController", "We listen! huh")
            when (destination.id) {
                Destinations.mapsFragment -> navigateToMapFragment()
            }
        }
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
        //Navigation of drawer now is being listened by navcontroller!
        navigationView.setupWithNavController(navController)
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
        /*
         * This doesn't work, it still recreates the fragment..
         * Got to take a look at Navigator
         */
        if (navController.currentDestination == navController.graph.get(Destinations.mapsFragment)) {
            Toast.makeText(this, "Do nothing", Toast.LENGTH_SHORT).show()
            //do nothing
        } else {
            Toast.makeText(this, "Go to map fragment..", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mapsFragment -> {
                Toast.makeText(this, "Map selected!", Toast.LENGTH_SHORT).show()
            }
            R.id.trashStatsFragment -> Toast.makeText(this, "Trash stats!", Toast.LENGTH_SHORT)
                .show()
            R.id.errorsFragment -> Toast.makeText(this, "Errors selected!", Toast.LENGTH_SHORT)
                .show()
            R.id.aboutFragment -> Toast.makeText(this, "About us selected!", Toast.LENGTH_SHORT)
                .show()
        }
        return true
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}