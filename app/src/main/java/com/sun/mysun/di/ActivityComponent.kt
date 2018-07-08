package com.sun.mysun.di

import com.sun.mysun.ui.SunInfoActivity
import dagger.Component

@ActivityScope
@Component(modules = [NetworkModule::class])
interface ActivityComponent {
    fun inject(activity: SunInfoActivity)
}