package com.example.cvpilot.paywall

import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import com.revenuecat.purchases.CustomerInfo

object RevenueCatManager {

    fun isProUser(callback: (Boolean) -> Unit) {
        Purchases.sharedInstance.getCustomerInfo(object : ReceiveCustomerInfoCallback {
            override fun onReceived(customerInfo: CustomerInfo) {
                val isPro = customerInfo.entitlements["pro_access"]?.isActive == true
                callback(isPro)
            }

            override fun onError(error: com.revenuecat.purchases.PurchasesError) {
                callback(false)
            }
        })
    }

    fun refreshInfo(callback: (Boolean) -> Unit) {
        Purchases.sharedInstance.invalidateCustomerInfoCache()
        isProUser(callback)
    }
}