package com.example.kalax

import android.app.Application
import com.example.kalax.di.AppContainer
import com.example.kalax.di.DefaultAppContainer

class KalaXApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
