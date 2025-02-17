package com.higame.sdk.core.model

data class PaymentParams(
    val orderId: String,
    val productId: String,
    val productName: String,
    val amount: Double,
    val currency: String = "CNY",
    val extraData: Map<String, Any>? = null
)

data class PaymentResult(
    val orderId: String,
    val status: PaymentStatus,
    val transactionId: String? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null
)

enum class PaymentStatus {
    SUCCESS,
    FAILED,
    CANCELLED,
    PENDING
}