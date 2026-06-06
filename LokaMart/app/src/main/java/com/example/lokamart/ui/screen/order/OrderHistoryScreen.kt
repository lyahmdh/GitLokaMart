package com.example.lokamart.ui.screen.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Order(
    val productName: String,
    val category: String,
    val quantity: Int,
    val totalPrice: Int,
    val date: String
)

@Composable
fun OrderHistoryScreen() {

    val orders = listOf(
        Order(
            productName = "Kopi Arabika Gayo 250g",
            category = "Kerajinan",
            quantity = 2,
            totalPrice = 150000,
            date = "12 Okt 2023"
        ),
        Order(
            productName = "Tas Anyaman Pandan",
            category = "Kerajinan",
            quantity = 1,
            totalPrice = 75000,
            date = "10 Okt 2023"
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(orders) { order ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = order.date,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = order.productName,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = order.category,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Qty: ${order.quantity}"
                    )

                    Text(
                        text = "Total: Rp ${order.totalPrice}"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Lihat Detail",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}