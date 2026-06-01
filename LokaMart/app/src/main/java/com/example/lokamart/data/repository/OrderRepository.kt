package com.example.lokamart.data.repository

import com.example.lokamart.data.model.Order
import com.example.lokamart.data.remote.SupabaseClient.client
import io.github.jan.supabase.postgrest.from

class OrderRepository {

    suspend fun createOrder(
        order: Order
    ): Result<Unit> {
        return runCatching {
            client
                .from("orders")
                .insert(order)
        }
    }

    suspend fun getMyOrders(
        userId: String
    ): Result<List<Order>> {
        return runCatching {
            client
                .from("orders")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<Order>()
        }
    }
}