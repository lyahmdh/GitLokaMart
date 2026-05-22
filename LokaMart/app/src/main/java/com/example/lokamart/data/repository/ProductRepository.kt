package com.example.lokamart.data.repository

import com.example.lokamart.data.model.Product
import com.example.lokamart.data.remote.SupabaseClient.client
import io.github.jan.supabase.postgrest.postgrest

// ✅ Fix: nama class diseragamkan jadi ProductRepository
//    (sebelumnya ada dua nama: HomeRepository di file, tapi ViewModel manggil ProductRepository)
class ProductRepository {

    suspend fun getProducts(): Result<List<Product>> {
        return try {
            val products = client
                .postgrest["products"]
                .select()
                .decodeList<Product>()
            Result.success(products)
        } catch (e: Exception) {
            // Tambahkan ini sementara
            android.util.Log.e("ProductRepository", "Error: ${e::class.simpleName} → ${e.message}", e)
            Result.failure(e)
        }
    }
}
