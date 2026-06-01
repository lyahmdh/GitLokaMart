package com.example.lokamart.data.repository

import com.example.lokamart.data.model.ProductImage
import com.example.lokamart.data.remote.SupabaseClient.client
import io.github.jan.supabase.postgrest.from

class ProductImageRepository {

    suspend fun getByProductId(
        productId: String
    ): Result<List<ProductImage>> {
        return runCatching {
            client
                .from("product_images")
                .select {
                    filter {
                        eq("product_id", productId)
                    }
                }
                .decodeList<ProductImage>()
                .sortedBy { it.sortOrder }
        }
    }

    suspend fun insertImages(
        images: List<ProductImage>
    ): Result<Unit> {
        return runCatching {
            client
                .from("product_images")
                .insert(images)
        }
    }

    suspend fun deleteByProductId(
        productId: String
    ): Result<Unit> {
        return runCatching {
            client
                .from("product_images")
                .delete {
                    filter {
                        eq("product_id", productId)
                    }
                }
        }
    }
}