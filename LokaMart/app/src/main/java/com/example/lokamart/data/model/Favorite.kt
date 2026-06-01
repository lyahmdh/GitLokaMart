package com.example.lokamart.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Favorite(

    @SerialName("user_id")
    val userId: String,

    @SerialName("product_id")
    val productId: String,

    @SerialName("created_at")
    val createdAt: String? = null
)
