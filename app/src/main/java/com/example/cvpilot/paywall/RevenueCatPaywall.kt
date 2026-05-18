package com.example.cvpilot.paywall

import androidx.compose.runtime.Composable
import com.revenuecat.purchases.ui.revenuecatui.Paywall
import com.revenuecat.purchases.ui.revenuecatui.PaywallListener
import com.revenuecat.purchases.ui.revenuecatui.PaywallOptions
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.PurchasesError

@Composable
fun RevenueCatPaywall(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    Paywall(
        // 🔥 FIX: Pass the dismiss lambda directly into the Builder constructor parameter
        options = PaywallOptions.Builder(dismissRequest = onDismiss)
            .setListener(object : PaywallListener {
                override fun onPurchaseCompleted(
                    customerInfo: CustomerInfo,
                    storeTransaction: StoreTransaction
                ) {
                    // Verifies active entitlement status before calling success
                    if (customerInfo.entitlements["premium"]?.isActive == true ||
                        customerInfo.entitlements["pro"]?.isActive == true) {
                        onSuccess()
                    }
                }

                override fun onPurchaseError(error: PurchasesError) {
                    android.util.Log.e("CV_PAYWALL", "Purchase failed: ${error.message}")
                }

                override fun onRestoreCompleted(customerInfo: CustomerInfo) {
                    if (customerInfo.entitlements["premium"]?.isActive == true ||
                        customerInfo.entitlements["pro"]?.isActive == true) {
                        onSuccess()
                    }
                }
            })
            .build()
    )
}