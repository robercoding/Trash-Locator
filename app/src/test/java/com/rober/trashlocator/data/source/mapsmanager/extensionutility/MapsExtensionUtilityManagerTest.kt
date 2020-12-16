package com.rober.trashlocator.data.source.mapsmanager.extensionutility

import android.location.Location
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth
import com.rober.trashlocator.data.source.mapsmanager.utils.TrashLocationUtils
import com.rober.trashlocator.models.AddressLocation
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config


@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(AndroidJUnit4::class)
class MapsExtensionUtilityManagerTest {

    lateinit var fakeMapsExtensionUtilityManager: FakeMapsExtensionUtilityManager
    lateinit var trashLocationUtils: TrashLocationUtils

    @Before
    fun setup() {
        trashLocationUtils =
            TrashLocationUtils(InstrumentationRegistry.getInstrumentation().targetContext)
        fakeMapsExtensionUtilityManager = FakeMapsExtensionUtilityManager(trashLocationUtils)
    }

    @Test
    fun setFeatureEmpty_returnTrashLocationWithEmptyFeature() {
        //Given
        fakeMapsExtensionUtilityManager.setShouldAddTrashLocationFeature(false)

        //When
        val location = Location("")
        location.latitude = 39.4754331
        val trashLocation = fakeMapsExtensionUtilityManager.getSingleTrashLocation(location)

        //Then
        Truth.assertThat(trashLocation.feature).isEmpty()
    }

    @Test
    fun setFeature_returnTrashLocationWithFeature() {
        //Given
        fakeMapsExtensionUtilityManager.setShouldAddTrashLocationFeature(true)

        //When
        val location = Location("")
        location.latitude = 39.4754331
        val trashLocation = fakeMapsExtensionUtilityManager.getSingleTrashLocation(location)

        //Then
        Truth.assertThat(trashLocation.feature).isNotEmpty()
    }

    @Test
    fun passLatitude_returnTrashLocationLocalityCaceres() {
        //Given
        fakeMapsExtensionUtilityManager.setShouldAddTrashLocationFeature(true)
        fakeMapsExtensionUtilityManager.setShouldAddTrashLocationLocality(true)

        //When
        val location = Location("")
        location.latitude = 39.4754331
        val trashLocation = fakeMapsExtensionUtilityManager.getSingleTrashLocation(location)

        //Then
        Truth.assertThat(trashLocation.locality).isEqualTo("Cáceres")
    }

    @Test
    fun setLocationSantaCruzDeTenerife_returnTrashLocationSantaCruzDeTenerife() {
        //Given
        fakeMapsExtensionUtilityManager.setShouldAddTrashLocationFeature(true)
        fakeMapsExtensionUtilityManager.setShouldAddTrashLocationLocality(true)

        //When
        val location = Location("")
        location.latitude = 28.4633765
        val trashLocation = fakeMapsExtensionUtilityManager.getSingleTrashLocation(location)

        //Then
        Truth.assertThat(trashLocation.locality).isEqualTo("Santa Cruz de Tenerife")
    }

    @Test
    fun setWithLocality_returnTrashLocationLocality() {
        //Given
        fakeMapsExtensionUtilityManager.setShouldAddTrashLocationLocality(true)

        //When
        val location = Location("")
        location.latitude = 28.4633765
        val trashLocation = fakeMapsExtensionUtilityManager.getSingleTrashLocation(location)

        //Then
        Truth.assertThat(trashLocation.locality).isEqualTo("Santa Cruz de Tenerife")
    }

    @Test
    fun setWithoutLocality_returnTrashLocationLocalityEmpty() {
        //Given
        fakeMapsExtensionUtilityManager.setShouldAddTrashLocationLocality(false)

        //When
        val location = Location("")
        location.latitude = 28.4633765
        val trashLocation = fakeMapsExtensionUtilityManager.getSingleTrashLocation(location)

        //Then
        Truth.assertThat(trashLocation.locality).isEmpty()
    }

    @Test
    fun setLocationWithLess100Distance_returnListWithTrash() = runBlockingTest {
        //Given
        val location = Location("")
        //Location caceres
        location.latitude = 39.4754331
        location.longitude = -6.377640
        val addressLocation = AddressLocation("Cáceres", "Cáceres", "Cáceres", location)

        //When
        val trashCluster = fakeMapsExtensionUtilityManager.getTrashCluster(null, addressLocation)

        //Then
        Truth.assertThat(trashCluster).isNotEmpty()
    }

    @Test
    fun setLocationWithLess100Distance_returnEmptyList() = runBlockingTest {
        //Given
        val location = Location("")
        //location that is far from caceres
        location.latitude = 9.4754331
        location.longitude = -5.377640
        val addressLocation =
            AddressLocation("Far from caceres", "Far from caceres", "Far from caceres", location)

        //When
        val trashCluster = fakeMapsExtensionUtilityManager.getTrashCluster(null, addressLocation)

        //Then
        Truth.assertThat(trashCluster).isEmpty()
    }
}