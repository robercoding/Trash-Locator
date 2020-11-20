package com.rober.trashlocator.di

import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import com.rober.trashlocator.data.repository.maps.MapsRepositoryImpl
import com.rober.trashlocator.data.repository.permissions.PermissionsRepositoryImpl
import com.rober.trashlocator.ui.fragments.maps.utils.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides fun provideMapsManager(
        @ActivityContext context: Context,
        permissionsManager: PermissionsManager,
        gpsManager: GPSManager,
        geoCoderManager: MapsExtensionUtilityManager,
        locationManager: LocationManager
    ): MapsManager = MapsManager(context, permissionsManager, gpsManager,geoCoderManager,  locationManager)

    @Provides
    fun provideMapsRepository(mapsManager: MapsManager) = MapsRepositoryImpl(mapsManager)

    @Provides fun providePermissionsRepository(permissionsManager: PermissionsManager) = PermissionsRepositoryImpl(permissionsManager)

    @Provides
    fun providePermissionsManager(
        @ApplicationContext context: Context,
        gpsManager: GPSManager
    ): PermissionsManager = PermissionsManager(context, gpsManager)

    @Provides
    fun provideGPSManager(@ActivityContext context: Context, locationManager: LocationManager) =
        GPSManager(context, locationManager)

    @Provides
    fun provideMapsExtensionUtilityManager(
        @ApplicationContext context: Context,
        geoCoder: Geocoder,
        trashLocationUtils: TrashLocationUtils
    ) = MapsExtensionUtilityManager(context, geoCoder, trashLocationUtils)

    @Provides fun provideGeoCoder(@ActivityContext context: Context) = Geocoder(context)

    @Provides fun provideTrashLocationUtils(@ActivityContext context: Context) = TrashLocationUtils(context)

    @Provides
    fun provideLocationManager(@ApplicationContext context: Context): LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
}