package com.rober.trashlocator.di

import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import com.google.android.gms.location.LocationServices
import com.rober.trashlocator.data.repository.maps.MapsRepositoryImpl
import com.rober.trashlocator.data.repository.permissions.PermissionsRepositoryImpl
import com.rober.trashlocator.data.source.mapsmanager.utils.TrashLocationUtils
import com.rober.trashlocator.data.source.mapsmanager.utils.gpsmanager.GPSManager
import com.rober.trashlocator.data.source.mapsmanager.MapsManager
import com.rober.trashlocator.data.source.mapsmanager.extensionutility.MapsExtensionUtilityManager
import com.rober.trashlocator.data.source.mapsmanager.utils.CustomLocationManager
import com.rober.trashlocator.data.source.mapsmanager.utils.permissions.PermissionsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ActivityComponent::class)
object GoogleMapModule {

    @Provides
    fun provideMapsManager(
        @ActivityContext context: Context,
        permissionsManager: PermissionsManager,
        gpsManager: GPSManager,
        geoCoderManager: MapsExtensionUtilityManager,
        locationManager: CustomLocationManager
    ): MapsManager =
        MapsManager(context, permissionsManager, gpsManager, geoCoderManager, locationManager)

    @Provides
    fun provideMapsRepository(mapsManager: MapsManager) = MapsRepositoryImpl(mapsManager)

    @Provides
    fun providePermissionsRepository(permissionsManager: PermissionsManager) =
        PermissionsRepositoryImpl(permissionsManager)

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

    @Provides
    fun provideGeoCoder(@ActivityContext context: Context) = Geocoder(context)

    @Provides
    fun provideTrashLocationUtils(@ActivityContext context: Context) = TrashLocationUtils(context)

    @Provides
    fun provideCustomLocationManager(locationManager: LocationManager) = CustomLocationManager(locationManager)

    @Provides
    fun provideLocationManager(@ActivityContext context: Context): LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @Provides
    fun provideFusedLocationProvider(@ActivityContext context: Context) =
        LocationServices.getFusedLocationProviderClient(context)
}