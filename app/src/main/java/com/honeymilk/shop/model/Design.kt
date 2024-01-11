package com.honeymilk.shop.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Design(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String = "",
    val group: String = "",
    val imageURL: String = "",
    val userId: String = "",
    @ServerTimestamp val createdAt: Date = Date()
)
