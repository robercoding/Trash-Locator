package com.rober.trashlocator.di

import com.rober.trashlocator.ui.fragments.maps.utils.IGPSManagerListener
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
abstract class ListenersModule {

    @Binds
    abstract fun bindIGPSManagerListener() : IGPSManagerListener
}