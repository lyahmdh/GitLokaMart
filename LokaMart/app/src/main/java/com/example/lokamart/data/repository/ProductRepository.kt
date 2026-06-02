package com.example.lokamart.data.repository

import android.util.Log
import com.example.lokamart.data.model.Product
import com.example.lokamart.data.remote.SupabaseClient.client
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns

class ProductRepository {

    suspend fun getProducts(): Result<List<Product>> {
        return try {

            val products = client
                .postgrest["products"]
                .select(
                    columns = Columns.raw(
                        """
                        *,
                        product_images(*)
                        """.trimIndent()
                    )
                )
                .decodeList<Product>()

            Log.d(
                "ProductRepository",
                "Products loaded: ${products.size}"
            )

            Result.success(products)

        } catch (e: Exception) {

            Log.e(
                "ProductRepository",
                "Error: ${e.message}",
                e
            )

            Result.failure(e)
        }
    }

    // ── FIX: tambah product_images(*) agar foto ikut ter-load ──
    suspend fun getProductById(
        productId: String
    ): Result<Product> {
        return runCatching {
            client
                .from("products")
                .select(
                    columns = Columns.raw(
                        """
                        *,
                        product_images(*)
                        """.trimIndent()
                    )
                ) {
                    filter {
                        eq("id", productId)
                    }
                }
                .decodeSingle<Product>()
        }
    }

    suspend fun getMyProducts(
        userId: String
    ): Result<List<Product>> {
        return runCatching {
            client
                .from("products")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<Product>()
        }
    }

    suspend fun createProduct(
        product: Product
    ): Result<Unit> {
        return runCatching {
            client
                .from("products")
                .insert(product)
        }
    }

    suspend fun updateProduct(
        product: Product
    ): Result<Unit> {
        return runCatching {
            client
                .from("products")
                .update(product) {
                    filter {
                        eq("id", product.id)
                    }
                }
        }
    }

    suspend fun archiveProduct(
        productId: String
    ): Result<Unit> {
        return runCatching {
            client
                .from("products")
                .update(
                    mapOf(
                        "is_archived" to true
                    )
                ) {
                    filter {
                        eq("id", productId)
                    }
                }
        }
    }
}