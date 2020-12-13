package com.rober.trashlocator.ui.fragments.maps

import android.content.Context
import android.location.LocationManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.TypeTextAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnitRunner
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsClient
import com.rober.trashlocator.R
import com.rober.trashlocator.ToastMatcher
import com.rober.trashlocator.data.source.mapsmanager.utils.gps.GpsUtilsImpl
import com.rober.trashlocator.launchFragmentInHiltContainer
import com.rober.trashlocator.utils.EspressoIdlingResource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@HiltAndroidTest
class MapsFragmentTest :
    AndroidJUnitRunner() { //Don't launch them once at all because it crashes, launch one test manually

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    lateinit var gpsUtils: GpsUtilsImpl
    lateinit var locationManager: LocationManager
    lateinit var settingsClient: SettingsClient

    lateinit var device: UiDevice

    @Before
    fun setup() {
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

        device =
            UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()) //Init automator to enable GPS

        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        launchFragmentInHiltContainer<MapsFragment>()

        if(!isGpsEnabled()){
            performClickEnableGPS()
        }
    }

    @After
    fun unRegisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun writeOnSearchPlace_andVerifyItsWritten() = runBlockingTest {
        val stringToTest = "Madrid"
        onView(withId(R.id.ETsearchLocation)).perform(TypeTextAction(stringToTest))
        onView(withId(R.id.ETsearchLocation)).check(matches(withText(stringToTest)))
    }

    @Test
    fun writeOnSearchPlace_clickOnRecyclerView_moveCameraPosition_displaysToastNotFoundDataSet() =
    runBlockingTest {
            //Given
            val stringToTest = "Madrid"
            onView(withId(R.id.ETsearchLocation)).perform(TypeTextAction(stringToTest))

            //When
            onView(withId(R.id.ETsearchLocation)).check(matches(withText(stringToTest)))

            //Then
            onView(withId(R.id.rowAddress)).check(matches(isDisplayed()))
            onView(withId(R.id.rowAddress)).perform(click())

            onView(withText(InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.dataset_not_found))).inRoot(
                ToastMatcher()
            ).check(
                matches(
                    isDisplayed()
                )
            )
        }

    @Test
    fun writeOnSearchPlace_clickOnRecyclerView_moveCameraPosition_displaysToastFoundDataSet() =
        runBlockingTest {
            //Given
            val stringToTest = "SantaCruzTenerife"

            onView(withId(R.id.ETsearchLocation)).perform(TypeTextAction(stringToTest))

            //When
            onView(withId(R.id.ETsearchLocation)).check(matches(withText(stringToTest)))

            //Then
            onView(withId(R.id.rowAddress)).perform(click())

            onView(withText(InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.dataset_found))).inRoot(
                ToastMatcher()
            ).check(
                matches(
                    isDisplayed()
                )
            )
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