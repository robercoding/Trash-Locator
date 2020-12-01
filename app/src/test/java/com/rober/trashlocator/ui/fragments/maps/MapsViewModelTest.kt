package com.rober.trashlocator.ui.fragments.maps

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.rober.trashlocator.data.repository.maps.MapsRepositoryImpl
import com.rober.trashlocator.data.repository.permissions.PermissionsRepositoryImpl
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.data.source.mapsmanager.MapsManager
import com.rober.trashlocator.utils.Event
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {
    @Rule @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var viewmodel : MapsViewModel

    @Mock lateinit var mapsRepositoryImpl: MapsRepositoryImpl
    @Mock lateinit var permissionsRepositoryImpl: PermissionsRepositoryImpl
    @Mock lateinit var mapsManager : MapsManager

    @Mock lateinit var listAddressesObserver: Observer<Event<List<AddressLocation>>>
    @Mock lateinit var observer : Observer<Event<List<AddressLocation>>>


    @Before
    fun setup(){
//        permissionsRepositoryImpl = mock(PermissionsRepositoryImpl::class.java)
//        mapsManager = mock(MapsManager::class.java)
//        mapsManager = MapsManager()
        mapsRepositoryImpl = MapsRepositoryImpl(mapsManager)
        viewmodel = MapsViewModel(mapsRepositoryImpl, permissionsRepositoryImpl)
        viewmodel.listAddressesLocation.observeForever(listAddressesObserver)
    }

//    @Test
//    fun checkObservers(){
//        launchFragmentInContainer<MapsFragment>()
//        val addressLocation = Event(listOf<AddressLocation>(AddressLocation("testviewmodel", "test", "mapsrepository", Location(""))))
//        mapsRepositoryImpl..value = addressLocation
//    }
}