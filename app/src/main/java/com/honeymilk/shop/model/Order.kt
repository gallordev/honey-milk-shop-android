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
    val shippingPaid: Boolean = false,
    val trackingCode: String = "",
    @ServerTimestamp val createdAt: Date = Date()
) {
    @Exclude
    fun getTotalItemsQuantity(): Int {
        val totalQuantity = items.sumOf { it.quantity }
        return totalQuantity
    }

    @Exclude
    fun getOrderItemsTotal(): Float {
        var total = 0f
        items.forEach { orderItem ->
            total += orderItem.getItemTotal()
        }
        return total
    }

    @Exclude
    fun getSubtotal(): Float = 0F
        .plus(getOrderItemsTotal())
        .plus(extrasTotal)

    @Exclude
    fun getTotal(): Float = 0F
        .plus(getOrderItemsTotal())
        .plus(extrasTotal)
        .plus(shippingPrice)

}
