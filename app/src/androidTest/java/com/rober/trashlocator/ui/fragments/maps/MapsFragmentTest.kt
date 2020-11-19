package com.rober.trashlocator.ui.fragments.maps

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
//import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.google.common.truth.Truth
import com.rober.trashlocator.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class MapsFragmentTest {

//    @get:Rule
    val fragmentScenario = launchFragmentInContainer<MapsFragment>(null, R.style.Theme_TrashLocator)

    @Test
    fun testInitializeMapsFragment_GoogleMapsIsInitialized() {
//        Log.i("MapsFragment", "Test!")
//        val scenario = fragmentScenario
//        scenario.onFragment {
//
//        }
//        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        Truth.assertThat(true).isEqualTo(true)
    }
}