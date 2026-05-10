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
                override fun onPurchaseCompleted(
                    customerInfo: CustomerInfo,
                    storeTransaction: com.revenuecat.purchases.models.StoreTransaction
                ) {
                    // Check if the entitlement is active before closing
                    if (customerInfo.entitlements["pro"]?.isActive == true) {
                        onSuccess()
                    }
                }

                override fun onPurchaseError(error: com.revenuecat.purchases.PurchasesError) {
                    // Optional: Show a snackbar or log the error
                    android.util.Log.e("CV_PAYWALL", "Purchase failed: ${error.message}")
                }

                override fun onRestoreCompleted(customerInfo: CustomerInfo) {
                    if (customerInfo.entitlements["pro"]?.isActive == true) {
                        onSuccess()
                    }
                }
            })
            .build()
    )
}