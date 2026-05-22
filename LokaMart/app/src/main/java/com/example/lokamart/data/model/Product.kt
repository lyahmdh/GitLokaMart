package com.example.lokamart.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,

    @SerialName("store_id")
    val storeId: String,

    val name: String,
    val description: String? = null,
    val price: Int,
    val category: String,

    @SerialName("image_url")
    val imageUrl: String? = null,

    val stock: Int,

    // ── Field baru ──────────────────────────────
    // Tambahkan kolom ini ke tabel products di Supabase
    val rating: Float = 0f,

    @SerialName("review_count")
    val reviewCount: Int = 0,

    @SerialName("store_name")
    val storeName: String? = null,
    // ────────────────────────────────────────────

    @SerialName("created_at")
    val createdAt: String
)
