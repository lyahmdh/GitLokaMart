package com.example.lokamart.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class Product(
    val id: String,

    @SerialName("user_id")
    val userId: String,

    val name: String,

    val description: String? = null,

    val price: Int,

    val category: String,

    val stock: Int,

    @SerialName("is_archived")
    val isArchived: Boolean = false,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String? = null,

    @SerialName("product_images")
    val productImages: List<ProductImage> = emptyList()
)

val Product.thumbnailUrl: String?
    get() = productImages
        .firstOrNull { it.isThumbnail }
        ?.imageUrl