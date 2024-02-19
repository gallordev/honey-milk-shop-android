package com.honeymilk.shop.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.UUID

data class Presentation(
    val name: String = "",
    val price: Float = 0F
) {
    companion object {
        private const val NAME = "name"
        private const val PRICE = "price"
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            NAME to name,
            PRICE to price
        )
    }
}
