package com.rober.trashlocator.ui.fragments.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.rober.trashlocator.data.repository.maps.MapsRepositoryImpl
import com.rober.trashlocator.data.source.mapsmanager.MapsManagerImpl
import com.rober.trashlocator.data.source.mapsmanager.utils.permissions.PermissionsUtilsImpl
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.utils.Event
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var viewmodel: MapsViewModel

    @Mock
    lateinit var mapsRepositoryImpl: MapsRepositoryImpl

    @Mock
    lateinit var permissionsManagerImpl: PermissionsUtilsImpl

    @Mock
    lateinit var mapsManager: MapsManagerImpl

    @Mock
    lateinit var listAddressesObserver: Observer<Event<List<AddressLocation>>>

    @Before
    fun setup() {
        mapsRepositoryImpl = MapsRepositoryImpl(mapsManager)
        viewmodel = MapsViewModel(mapsRepositoryImpl, permissionsManagerImpl)
        viewmodel.listAddressesLocation.observeForever(listAddressesObserver)
    }
}