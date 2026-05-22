package com.example.lokamart.data.repository

import com.example.lokamart.data.model.Product
import com.example.lokamart.data.remote.SupabaseClient.client
import io.github.jan.supabase.postgrest.postgrest

class HomeRepository {

    suspend fun getProducts(): Result<List<Product>> {
        return try {

            val products = client
                .postgrest["products"]
                .select()
                .decodeList<Product>()

            Result.success(products)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}