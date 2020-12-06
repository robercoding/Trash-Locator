package com.rober.trashlocator.di

import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import com.google.android.gms.location.LocationServices
import com.rober.trashlocator.data.repository.maps.MapsRepositoryImpl
import com.rober.trashlocator.data.source.mapsmanager.utils.TrashLocationUtils
import com.rober.trashlocator.data.source.mapsmanager.utils.gpsmanager.GPSManagerImpl
import com.rober.trashlocator.data.source.mapsmanager.MapsManagerImpl
import com.rober.trashlocator.data.source.mapsmanager.extensionutility.MapsExtensionUtilityManagerImpl
import com.rober.trashlocator.data.source.mapsmanager.utils.CustomLocationManagerImpl
import com.rober.trashlocator.data.source.mapsmanager.utils.permissions.PermissionsManagerImpl
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
        permissionsManager: PermissionsManagerImpl,
        gpsManager: GPSManagerImpl,
        geoCoderManager: MapsExtensionUtilityManagerImpl,
        locationManager: CustomLocationManagerImpl
    ): MapsManagerImpl =
        MapsManagerImpl(context, permissionsManager, gpsManager, geoCoderManager, locationManager)

    @Provides
    fun provideMapsRepository(mapsManager: MapsManagerImpl) = MapsRepositoryImpl(mapsManager)

    @Provides
    fun providePermissionsManager(
        @ApplicationContext context: Context,
        gpsManager: GPSManagerImpl
    ): PermissionsManagerImpl =
        PermissionsManagerImpl(context, gpsManager)

    @Provides
    fun provideGPSManager(@ActivityContext context: Context, locationManager: LocationManager) =
        GPSManagerImpl(context, locationManager)

    @Provides
    fun provideMapsExtensionUtilityManager(
        @ApplicationContext context: Context,
        geoCoder: Geocoder,
        trashLocationUtils: TrashLocationUtils
    ) = MapsExtensionUtilityManagerImpl(context, geoCoder, trashLocationUtils)

    @Provides
    fun provideGeoCoder(@ActivityContext context: Context) = Geocoder(context)

    @Provides
    fun provideTrashLocationUtils(@ActivityContext context: Context) = TrashLocationUtils(context)

    @Provides
    fun provideCustomLocationManager(locationManager: LocationManager) = CustomLocationManagerImpl(locationManager)

    @Provides
    fun provideLocationManager(@ActivityContext context: Context): LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @Provides
    fun provideFusedLocationProvider(@ActivityContext context: Context) =
        LocationServices.getFusedLocationProviderClient(context)
}