package com.honeymilk.shop.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.UUID

@IgnoreExtraProperties
data class OrderItem(
    @Exclude
    val id: String = UUID.randomUUID().toString(),
    val design: Design = Design(),
    val color: String = "",
    val type: String = "",
    var size: String = "",
    var quantity: Int = 1
) {

    @Exclude
    fun increaseQuantity() {
        if (quantity in 1..99) {
            quantity += 1
        }
    }

    @Exclude
    fun decreaseQuantity() {
        if (quantity > 1) {
            quantity -= 1
        }
    }

}
