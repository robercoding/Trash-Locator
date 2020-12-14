package com.rober.trashlocator.data.source.mapsmanager.utils

import android.location.Location
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth
import com.rober.trashlocator.R
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.models.TrashLocation
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(AndroidJUnit4::class)
class TrashLocationUtilsTest {

    lateinit var trashLocationUtils: TrashLocationUtils

    @Before
    fun setup(){
        trashLocationUtils = TrashLocationUtils(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Test
    fun passLocalityName_notFindDataSet(){
        //Given
        val addressLocation = AddressLocation("Dataset not found", "Dataset not found", "Dataset not found", Location(""))

        //When
        val result = trashLocationUtils.getDataset(addressLocation)

        //Then
        Truth.assertThat(result).isEqualTo(-1)
    }

    @Test
    fun passLocalityName_findDataSet(){
        //Given
        val addressLocation = AddressLocation("Cáceres\u200E", "Extremadura", "", Location(""))

        //When
        val result = trashLocationUtils.getDataset(addressLocation)

        //Then
        Truth.assertThat(result).isGreaterThan(0)
    }

    @Test
    fun passStreetNameAndFeatureAndLocality_returnTrashLocationWithTwoCommas(){
        //Given
        val trashLocation = TrashLocation("Street 3", "28", "Valencia")
        val expected = TrashLocation("Street 3, ","28, ", "Valencia")

        //When
        val result = trashLocationUtils.addCommaTrashLocation(trashLocation, InstrumentationRegistry.getInstrumentation().targetContext)

        //Then
        Truth.assertThat(result).isEqualTo(expected)
    }

    @Test
    fun passStreetNameAndLocality_returnTrashLocationWithOneComma(){
        //Given
        val trashLocation = TrashLocation("Street 3", "", "Valencia")
        val expected = TrashLocation("Street 3, ","", "Valencia")

        //When
        val result = trashLocationUtils.addCommaTrashLocation(trashLocation, InstrumentationRegistry.getInstrumentation().targetContext)

        //Then
        Truth.assertThat(result).isEqualTo(expected)
    }

    @Test
    fun passStreetName_returnTrashLocationWithZeroComma(){
        //Given
        val trashLocation = TrashLocation("Street 3", "", "")
        val expected = TrashLocation("Street 3","", "")

        //When
        val result = trashLocationUtils.addCommaTrashLocation(trashLocation, InstrumentationRegistry.getInstrumentation().targetContext)

        //Then
        Truth.assertThat(result).isEqualTo(expected)
    }

    @Test
    fun passEmpty_returnTrashLocationWithMessageNoInformationAboutTrash(){
        //Given
        val trashLocation = TrashLocation("", "", "")
        val expected = TrashLocation(InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.trash_no_information),"", "")

        //When
        val result = trashLocationUtils.addCommaTrashLocation(trashLocation, InstrumentationRegistry.getInstrumentation().targetContext)

        //Then
        Truth.assertThat(result).isEqualTo(expected)
    }

    @Test
    fun passLocality_returnTrashLocationWithMessageNoInformationAboutTrash(){
        //Given
        val trashLocation = TrashLocation("", "", "Valencia")
        val expected = TrashLocation(InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.trash_no_information),"", "")

        //When
        val result = trashLocationUtils.addCommaTrashLocation(trashLocation, InstrumentationRegistry.getInstrumentation().targetContext)

        //Then
        Truth.assertThat(result).isEqualTo(expected)
    }

    @Test
    fun passFeatureAndLocality_returnTrashLocationWithOneComma(){
        //Given
        val trashLocation = TrashLocation("", "28", "Valencia")
        val expected = TrashLocation("","28, ", "Valencia")

        //When
        val result = trashLocationUtils.addCommaTrashLocation(trashLocation, InstrumentationRegistry.getInstrumentation().targetContext)

        //Then
        Truth.assertThat(result).isEqualTo(expected)
    }
}