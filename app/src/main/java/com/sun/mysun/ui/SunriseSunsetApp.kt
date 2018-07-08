package com.sun.mysun.ui

import android.app.Application
import timber.log.Timber

class SunriseSunsetApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}
