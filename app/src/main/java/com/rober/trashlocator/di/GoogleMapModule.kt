package com.rober.trashlocator.di

import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsClient
import com.rober.trashlocator.data.repository.maps.MapsRepositoryImpl
import com.rober.trashlocator.data.source.mapsmanager.MapsManagerImpl
import com.rober.trashlocator.data.source.mapsmanager.extensionutility.MapsExtensionUtilityImpl
import com.rober.trashlocator.data.source.mapsmanager.utils.CustomLocationManagerImpl
import com.rober.trashlocator.data.source.mapsmanager.utils.TrashLocationUtils
import com.rober.trashlocator.data.source.mapsmanager.utils.gps.GpsUtilsImpl
import com.rober.trashlocator.data.source.mapsmanager.utils.permissions.PermissionsUtilsImpl
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
        permissionsManager: PermissionsUtilsImpl,
        gpsUtils: GpsUtilsImpl,
        geoCoderManager: MapsExtensionUtilityImpl,
        locationManager: CustomLocationManagerImpl
    ): MapsManagerImpl =
        MapsManagerImpl(context, permissionsManager, gpsUtils, geoCoderManager, locationManager)

    @Provides
    fun provideMapsRepository(mapsManager: MapsManagerImpl) = MapsRepositoryImpl(mapsManager)

    @Provides
    fun providePermissionsManager(
        @ActivityContext context: Context,
        gpsUtils: GpsUtilsImpl
    ): PermissionsUtilsImpl =
        PermissionsUtilsImpl(context, gpsUtils)

    @Provides
    fun provideGpsUtils(
        @ActivityContext context: Context,
        locationManager: LocationManager,
        settingsClient: SettingsClient
    ) =
        GpsUtilsImpl(context, locationManager, settingsClient)

    @Provides
    fun provideSettingsClient(@ActivityContext context: Context): SettingsClient =
        LocationServices.getSettingsClient(context)

    @Provides
    fun provideMapsExtensionUtilityManager(
        @ApplicationContext context: Context,
        geoCoder: Geocoder,
        trashLocationUtils: TrashLocationUtils
    ) = MapsExtensionUtilityImpl(context, geoCoder, trashLocationUtils)

    @Provides
    fun provideGeoCoder(@ActivityContext context: Context) = Geocoder(context)

    @Provides
    fun provideTrashLocationUtils(@ActivityContext context: Context) = TrashLocationUtils(context)

    @Provides
    fun provideCustomLocationManager(locationManager: LocationManager) =
        CustomLocationManagerImpl(locationManager)

    @Provides
    fun provideLocationManager(@ActivityContext context: Context): LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @Provides
    fun provideFusedLocationProvider(@ActivityContext context: Context) =
        LocationServices.getFusedLocationProviderClient(context)
}