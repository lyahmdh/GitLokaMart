package com.example.lokamart.data.repository

import com.example.lokamart.data.model.Favorite
import com.example.lokamart.data.remote.SupabaseClient.client
import io.github.jan.supabase.postgrest.from

class FavoriteRepository {

    suspend fun getMyFavorites(
        userId: String
    ): Result<List<Favorite>> {
        return runCatching {
            client
                .from("favorites")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList()
        }
    }

    suspend fun addFavorite(
        favorite: Favorite
    ): Result<Unit> {
        return runCatching {
            client
                .from("favorites")
                .insert(favorite)
        }
    }

    suspend fun removeFavorite(
        userId: String,
        productId: String
    ): Result<Unit> {
        return runCatching {
            client
                .from("favorites")
                .delete {
                    filter {
                        eq("user_id", userId)
                        eq("product_id", productId)
                    }
                }
        }
    }
}