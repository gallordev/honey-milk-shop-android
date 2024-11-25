package com.honeymilk.shop.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.honeymilk.shop.utils.Extensions.toCurrencyFormat
import kotlin.math.abs

@IgnoreExtraProperties
data class OrderItem(
    @Exclude val id: Int = abs((0..999999999999).random()).toInt() ,
    var design: Design = Design(),
    var color: String = "",
    var type: String = "",
    var size: String = "",
    var quantity: Int = 1,
    var comment: String = ""
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

    @Exclude
    fun getTypePrice(): Float = design.presentations.find { it.name == type }?.price ?: 0f

    @Exclude
    fun getItemTotal(): Float = getTypePrice().times(quantity)

}
