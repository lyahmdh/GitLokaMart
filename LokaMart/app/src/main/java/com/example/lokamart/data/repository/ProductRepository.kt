package com.example.lokamart.data.repository

import android.util.Log
import com.example.lokamart.data.model.Product
import com.example.lokamart.data.remote.SupabaseClient.client
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID

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
            Log.d("ProductRepository", "Products loaded: ${products.size}")
            Result.success(products)
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getProductById(productId: String): Result<Product> {
        return runCatching {
            client
                .from("products")
                .select {
                    filter { eq("id", productId) }
                }
                .decodeSingle<Product>()
        }
    }

    suspend fun getMyProducts(userId: String): Result<List<Product>> {
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
                    filter { eq("user_id", userId) }
                }
                .decodeList<Product>()
        }
    }

    suspend fun createProduct(product: Product): Result<Product> {
        return runCatching {
            val response = client.from("products")
                .insert(product) {
                    select()
                }
            response.decodeSingle<Product>()
        }
    }

    suspend fun uploadProductImage(bytes: ByteArray, fileExtension: String = "jpg"): Result<String> {
        return runCatching {
            val fileName = "${UUID.randomUUID()}.$fileExtension"
            val bucket = client.storage["product-images"]
            bucket.upload(fileName, bytes)
            bucket.publicUrl(fileName)
        }
    }

    suspend fun insertProductImage(productId: String, imageUrl: String): Result<Unit> {
        return runCatching {
            client.from("product_images").insert(
                buildJsonObject {
                    put("product_id", productId)
                    put("image_url", imageUrl)
                    put("is_thumbnail", true)
                }
            )
        }
    }

    suspend fun updateProduct(product: Product): Result<Unit> {
        return runCatching {
            client
                .from("products")
                .update(
                    buildJsonObject {
                        put("name", product.name)
                        put("category", product.category)
                        put("price", product.price)
                        put("description", product.description)
                        put("stock", product.stock)
                    }
                ) {
                    filter { eq("id", product.id) }
                }
        }
    }

    suspend fun archiveProduct(productId: String): Result<Unit> {
        return runCatching {
            client
                .from("products")
                .update(
                    buildJsonObject {
                        put("is_archived", true)
                    }
                ) {
                    filter { eq("id", productId) }
                }
        }
    }

    suspend fun unarchiveProduct(productId: String): Result<Unit> {
        return runCatching {
            client
                .from("products")
                .update(
                    buildJsonObject {
                        put("is_archived", false)
                    }
                ) {
                    filter { eq("id", productId) }
                }
        }
    }
}