package com.honeymilk.shop.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlin.math.abs
import kotlin.random.Random

@IgnoreExtraProperties
data class OrderItem(
    @Exclude
    val id: Int = abs((0..999999999999).random()).toInt() ,
    var design: Design = Design(),
    var color: String = "",
    var type: String = "",
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
