package com.example.lokamart.data.repository

import com.example.lokamart.data.model.Order
import com.example.lokamart.data.model.OrderWithProduct
import com.example.lokamart.data.remote.SupabaseClient.client
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order as SortOrder
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

    suspend fun getMyOrdersWithProduct(userId: String): Result<List<OrderWithProduct>> {
        return runCatching {
            client
                .from("orders")
                .select(
                    columns = Columns.raw(
                        """
                    *,
                    products(*, product_images(*))
                    """.trimIndent()
                    )
                ) {
                    filter { eq("user_id", userId) }
                    order("created_at", SortOrder.DESCENDING)
                }
                .decodeList<OrderWithProduct>()
        }
    }
}

