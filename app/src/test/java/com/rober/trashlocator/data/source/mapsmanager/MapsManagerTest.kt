package com.rober.trashlocator.data.source.mapsmanager

import android.Manifest
import android.location.Location
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.common.truth.Truth.assertThat
import com.rober.trashlocator.data.source.mapsmanager.extensionutility.FakeMapsExtensionUtility
import com.rober.trashlocator.data.source.mapsmanager.extensionutility.MapsExtensionUtility
import com.rober.trashlocator.data.source.mapsmanager.utils.FakeCustomLocationManager
import com.rober.trashlocator.data.source.mapsmanager.utils.TrashLocationUtils
import com.rober.trashlocator.data.source.mapsmanager.utils.gps.FakeGpsUtils
import com.rober.trashlocator.data.source.mapsmanager.utils.permissionsmanager.FakePermissionsUtils
import com.rober.trashlocator.getOrAwaitValue
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.utils.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(AndroidJUnit4::class)
class MapsManagerTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val grantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    lateinit var mapsManager: MapsManagerImpl
    lateinit var mapsExtensionUtility: MapsExtensionUtility
    lateinit var trashLocationUtils: TrashLocationUtils


    lateinit var permissionsManager: FakePermissionsUtils
    lateinit var gpsManager: FakeGpsUtils
    lateinit var locationManager: FakeCustomLocationManager

    @Before
    fun setup() {
        gpsManager = FakeGpsUtils()
        permissionsManager = FakePermissionsUtils()
        trashLocationUtils = TrashLocationUtils(ApplicationProvider.getApplicationContext())
        mapsExtensionUtility = FakeMapsExtensionUtility(trashLocationUtils)
        locationManager = FakeCustomLocationManager()
        mapsManager = MapsManagerImpl(
            ApplicationProvider.getApplicationContext(),
            permissionsManager,
            gpsManager,
            mapsExtensionUtility,
            locationManager
        )
    }

    @Test
    fun passLocationName_getAddressLocation() = runBlockingTest {
        val observer = Observer<Event<List<AddressLocation>>> {}
        //Given
        mapsManager.addressesLocation.observeForever(observer)
        mapsManager.getListAddressesByName("Madrid")
        //When
        val value = mapsManager.addressesLocation.getOrAwaitValue()
        //Then
        assertThat(value.getContentIfNotHandled()).isNotEmpty()
    }

    @Test
    fun passLocationName_emptyLocation() = runBlockingTest {
        val observer = Observer<Event<List<AddressLocation>>> {}
        //Given
        mapsManager.addressesLocation.observeForever(observer)
        mapsManager.getListAddressesByName("")
        //When
        val value = mapsManager.addressesLocation.getOrAwaitValue()
        //Then
        assertThat(value.getContentIfNotHandled()).isEmpty()
    }

    @Test
    fun updateNewAddressLocation_addToLiveData() = runBlockingTest {
        //Given
        val addressLocation = AddressLocation("Valencia", "Valencia", "Valencia", Location(""))

        //When
        val observer = Observer<AddressLocation> {}
        mapsManager.addressLocation.observeForever(observer)
        mapsManager.setUpdateLocationByAddressLocation(addressLocation, true)

        //Then
        val value = mapsManager.addressLocation.getOrAwaitValue()
        assertThat(value).isEqualTo(addressLocation)
    }

    @Test
    fun updateLocationByAddressLocation_NotAddToAddressLocation() = runBlockingTest {
        //Given
        val addressLocation = AddressLocation("Valencia", "Valencia", "Valencia", Location(""))
        val expectedAddressLocation = AddressLocation("null", "null", "null", Location(""))

        //When
        val observer = Observer<AddressLocation> {}
        mapsManager.addressLocation.observeForever(observer)
        mapsManager.setUpdateLocationByAddressLocation(addressLocation, false)

        //Then
        var value: AddressLocation? = null
//        value = mapsManager.addressLocation.getOrAwaitValue()
        try {
            value = mapsManager.addressLocation.getOrAwaitValue()
        } catch (e: Exception) {
        } finally {
            assertThat(value).isNull()
        }
    }

    @Test
    fun getDeviceLocation_GrantedPermissionsAndGps() {
        locationManager.setCustomLocationListener(mapsManager)
        permissionsManager.setReturnPermissionsOk(true)
        gpsManager.setGpsValue(true)

        mapsManager.updateLocationUI()
        var value: AddressLocation? = null
        try {
            value = mapsManager.addressLocation.getOrAwaitValue()
        } catch (e: Exception) {
        } finally {
            assertThat(value).isNotNull()
        }
    }

    @Test
    fun getDeviceLocation_NotGrantedPermissions() {
        locationManager.setCustomLocationListener(mapsManager)
        permissionsManager.setReturnPermissionsOk(false)
        gpsManager.setGpsValue(true)

        mapsManager.updateLocationUI()
        var value: AddressLocation? = null
        try {
            value = mapsManager.addressLocation.getOrAwaitValue()
        } catch (e: Exception) {
        } finally {
            assertThat(value).isNull()
        }
    }

    @Test
    fun getDeviceLocation_GpsDisabled() {
        locationManager.setCustomLocationListener(mapsManager)
        permissionsManager.setReturnPermissionsOk(true)
        gpsManager.setGpsValue(false)

        mapsManager.updateLocationUI()
        var value: AddressLocation? = null
        try {
            value = mapsManager.addressLocation.getOrAwaitValue()
        } catch (e: Exception) {
        } finally {
            assertThat(value).isNull()
        }
    }
}