package com.example.cvpilot

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CvPilotApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}