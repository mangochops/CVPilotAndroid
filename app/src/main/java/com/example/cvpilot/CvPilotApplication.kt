package com.example.cvpilot

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

@HiltAndroidApp
class CvPilotApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize RevenueCat here, not in Activity
        PDFBoxResourceLoader.init(applicationContext)
        Purchases.logLevel = com.revenuecat.purchases.LogLevel.DEBUG
        Purchases.configure(
            PurchasesConfiguration.Builder(this, BuildConfig.REVENUECAT_KEY).build()
        )
    }
}