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
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object GoogleMapsModule {
}