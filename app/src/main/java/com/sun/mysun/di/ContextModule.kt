package com.sun.mysun.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides

@Module
open class ContextModule(val context: Context) {

    @ActivityScope
    @Provides
    fun context(): Context = context.applicationContext

    @ActivityScope
    @Provides
    fun locationClient(): FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)
}