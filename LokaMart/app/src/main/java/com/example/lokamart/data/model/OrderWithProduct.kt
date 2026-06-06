package com.example.lokamart.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderWithProduct(
    val id: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("product_id")
    val productId: String,

    val quantity: Int,

    @SerialName("total_price")
    val totalPrice: Int,

    @SerialName("created_at")
    val createdAt: String? = null,

    val products: Product? = null   // hasil join
)