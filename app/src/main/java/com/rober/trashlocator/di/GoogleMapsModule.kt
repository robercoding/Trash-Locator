package com.rober.trashlocator.di

import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import com.rober.trashlocator.data.repository.maps.MapsRepositoryImpl
import com.rober.trashlocator.data.repository.permissions.PermissionsRepositoryImpl
import com.rober.trashlocator.ui.fragments.maps.utils.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object GoogleMapsModule {

    @Provides
    fun provideGeoCoderManager(
        @ApplicationContext context: Context,
        geocoder: Geocoder,
        trashLocationUtils: TrashLocationUtils
    ) = GeoCoderManager(context, geocoder, trashLocationUtils)

    @Provides fun provideGeoCoder(@ApplicationContext context: Context) = Geocoder(context)

    @Provides fun provideTrashLocationUtils(@ApplicationContext context: Context) = TrashLocationUtils(context)

    @Provides fun providePermissionsRepository(permissionsManager: PermissionsManager) = PermissionsRepositoryImpl(permissionsManager)

    @Provides
    fun provideMapsRepository(mapsManager: MapsManager) = MapsRepositoryImpl(mapsManager)

    @Provides
    fun provideMapsManager(
        @ActivityScoped context: Context,
        permissionsManager: PermissionsManager,
        gpsManager: GPSManager,
        locationManager: LocationManager
    ): MapsManager = MapsManager(context, permissionsManager, gpsManager, locationManager)

    @Singleton
    @Provides
    fun providePermissionsManager(
        @ApplicationContext context: Context,
        gpsManager: GPSManager
    ): PermissionsManager = PermissionsManager(context, gpsManager)

    @Provides
    fun provideGPSManager(@ApplicationContext context: Context, locationManager: LocationManager) =
        GPSManager(context, locationManager)

    @Singleton
    @Provides
    fun provideLocationManager(@ApplicationContext context: Context): LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager



}