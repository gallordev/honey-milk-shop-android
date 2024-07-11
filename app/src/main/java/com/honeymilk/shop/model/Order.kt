package com.honeymilk.shop.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date


@IgnoreExtraProperties
data class Order(
    @DocumentId val id: String = "",
    val customer: Customer = Customer(),
    val items: List<OrderItem> = emptyList(),
    val extras: String = "",
    val extrasTotal: Float = 0f,
    val shippingCompany: String = "",
    val shippingPrice: Float = 0f,
    val isShippingPaid: Boolean = false,
    val trackingCode: String = "",
    @ServerTimestamp val createdAt: Date = Date()
) {
    @Exclude
    fun getSubtotal(): Float {
        return 0f
    }

    @Exclude
    fun getTotal(): Float {
        return 0f
    }
}
