package com.rober.trashlocator.ui

import android.content.Context
import android.location.LocationManager
import androidx.navigation.NavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsClient
import com.google.common.truth.Truth.assertThat
import com.rober.trashlocator.R
import com.rober.trashlocator.data.source.mapsmanager.utils.gps.GpsUtilsImpl
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named


@LargeTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MapsActivityTest {
    //Test requires that location be activated or they will fail.
    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MapsActivity::class.java)

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    lateinit var gpsUtils: GpsUtilsImpl
    lateinit var locationManager: LocationManager
    lateinit var settingsClient: SettingsClient

    lateinit var device: UiDevice

    lateinit var navController: NavController

    @Before
    fun setupAndCheckGps() {
        //Setup
        hiltRule.inject()
        locationManager =
            InstrumentationRegistry.getInstrumentation().targetContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        settingsClient =
            LocationServices.getSettingsClient(InstrumentationRegistry.getInstrumentation().targetContext)
        gpsUtils = GpsUtilsImpl(
            InstrumentationRegistry.getInstrumentation().targetContext,
            locationManager,
            settingsClient
        )

        //Check GPS
        val scenario = activityScenarioRule.scenario
        scenario.onActivity { mapsActivity ->
            navController = mapsActivity.findNavController() //Init navController for the tests
        }

        device =
            UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()) //Init automator to enable GPS

        if (!isGpsEnabled()) {
            performClickEnableGPS()
        }
    }

    @Test
    fun startApp_WithMapsFragmentAsStartDestination() {
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.mapsFragment)
    }

    @Test
    fun clickOnDrawerMaps_NavigateToMapsFragment() {
        //Since start destination is MapsFragment we navigate to other fragment and then navigate to Maps
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open()).check(matches(isOpen()))
        onView(withId(R.id.aboutAppFragment)).perform(click())

        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open()).check(matches(isOpen()))
        onView(withId(R.id.mapsFragment)).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.mapsFragment)
    }

    @Test
    fun clickOnDrawerAbout_NavigateToAboutFragment() {
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open()).check(matches(isOpen()))
        onView(withId(R.id.aboutAppFragment)).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.aboutAppFragment)
    }

    @Test
    fun clickOnDrawerTrashStats_NavigateToTrashStatsFragment() {
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open()).check(matches(isOpen()))
        onView(withId(R.id.trashStatsFragment)).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.trashStatsFragment)
    }

    @Test
    fun clickOnDrawerContact_NavigateToContactFragment() {
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open()).check(matches(isOpen()))
        onView(withId(R.id.contactFragment)).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.contactFragment)
    }

    @Test
    fun clickOnDrawerSettings_NavigateToSettingsFragment() {
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open()).check(matches(isOpen()))
        onView(withId(R.id.settingsFragment)).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.settingsFragment)
    }

    private fun isGpsEnabled(): Boolean {
        if (!gpsUtils.isGPSEnabled()) {
            return false
        }
        return true
    }

    @Throws(UiObjectNotFoundException::class)
    private fun performClickEnableGPS() {
        val allowGpsBtn = device.findObject(
            UiSelector()
                .className("android.widget.Button").packageName("com.google.android.gms")
                .resourceId("android:id/button1")
                .clickable(true).checkable(false)
        )
        device.pressDelete() // just in case to turn ON blur screen (not a wake up) for some devices like HTC and some other
        if (allowGpsBtn.exists() && allowGpsBtn.isEnabled) {
            do {
                allowGpsBtn.click()
            } while (allowGpsBtn.exists())
        }
    }
}