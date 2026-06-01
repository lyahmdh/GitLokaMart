package com.example.lokamart.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductImage(

    val id: String,

    @SerialName("product_id")
    val productId: String,

    @SerialName("image_url")
    val imageUrl: String,

    @SerialName("is_thumbnail")
    val isThumbnail: Boolean = false,

    @SerialName("sort_order")
    val sortOrder: Int = 0
)