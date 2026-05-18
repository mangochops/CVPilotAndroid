package com.example.cvpilot.paywall

import android.util.Log
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import com.revenuecat.purchases.CustomerInfo

object RevenueCatManager {

    private const val DEBUG_MODE = true

    fun isProUser(callback: (Boolean) -> Unit) {
        if (DEBUG_MODE) {
            Log.d("CV_DEBUG", "RevenueCat: Mock Mode Active - Returning Pro = true")
            callback(true)
            return
        }

        try {
            Purchases.sharedInstance.getCustomerInfo(object : ReceiveCustomerInfoCallback {
                override fun onReceived(customerInfo: CustomerInfo) {
                    // Match with your "pro" identifier check used inside the Paywall component logic
                    val isPro = customerInfo.entitlements["pro"]?.isActive == true
                    callback(isPro)
                }

                override fun onError(error: com.revenuecat.purchases.PurchasesError) {
                    Log.e("CV_DEBUG", "RevenueCat Error: ${error.message}")
                    callback(false)
                }
            })
        } catch (e: Exception) {
            Log.e("CV_DEBUG", "RevenueCat not initialized yet: ${e.message}")
            callback(false)
        }
    }

    fun refreshInfo(callback: (Boolean) -> Unit) {
        if (DEBUG_MODE) {
            callback(true)
            return
        }
        Purchases.sharedInstance.invalidateCustomerInfoCache()
        isProUser(callback)
    }
}