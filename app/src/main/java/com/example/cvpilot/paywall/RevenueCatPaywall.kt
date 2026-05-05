package com.example.cvpilot.paywall

import androidx.compose.runtime.Composable
import com.revenuecat.purchases.ui.revenuecatui.Paywall
import com.revenuecat.purchases.ui.revenuecatui.PaywallListener
import com.revenuecat.purchases.ui.revenuecatui.PaywallOptions
import com.revenuecat.purchases.CustomerInfo

@Composable
fun RevenueCatPaywall(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    Paywall(
        options = PaywallOptions.Builder(dismissRequest = onDismiss)
            .setListener(object : PaywallListener {
                override fun onPurchaseCompleted(customerInfo: CustomerInfo, storeTransaction: com.revenuecat.purchases.models.StoreTransaction) {
                    onSuccess()
                }

                override fun onRestoreCompleted(customerInfo: CustomerInfo) {
                    onSuccess()
                }
            })
            .build()
    )
}