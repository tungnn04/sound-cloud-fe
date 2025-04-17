package com.example.app

import android.app.Application
import com.example.app.data.AppContainer
import com.example.app.data.DefaultAppContainer

class MusicApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(applicationContext)
    }
}