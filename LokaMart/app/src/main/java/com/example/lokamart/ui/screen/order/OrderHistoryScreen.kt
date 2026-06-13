package com.example.lokamart.ui.screen.order

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.lokamart.data.model.OrderWithProduct
import com.example.lokamart.data.model.thumbnailUrl
import com.example.lokamart.ui.screen.auth.GreenDark
import com.example.lokamart.ui.viewmodel.OrderHistoryViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner

private val TextPrimary   = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF757575)
private val GreenChip     = Color(0xFFDCEFDC)
private val BgColor       = Color(0xFFF7F7F7)

@Composable
fun OrderHistoryScreen(
    viewModel: OrderHistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow
        .collectAsStateWithLifecycle()

    // Kumpulkan kategori unik dari semua order
    val categories = listOf(
        "Semua", "Fashion", "Kerajinan Tangan", "Peralatan Rumah Tangga", "Dekorasi Ruangan"
    )

    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            viewModel.loadOrders()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        // ── Top Bar ───────────────────────────────────────────
        Surface(color = Color.White, shadowElevation = 2.dp) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "LokaMart",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenDark
                    )
                }

                // Filter chip dipindah ke sini
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val selected = cat == uiState.selectedCategory
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.filterByCategory(cat) },
                            label = { Text(cat, fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GreenDark,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = Color(0xFFE0E0E0),
                                selectedBorderColor = GreenDark
                            )
                        )
                    }
                }
            }
        }

        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenDark)
                }
            }

            uiState.errorMessage != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Gagal memuat pesanan",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = viewModel::loadOrders,
                            colors = ButtonDefaults.buttonColors(containerColor = GreenDark)
                        ) { Text("Coba Lagi") }
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp,
                        top = 20.dp, bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ── Judul ─────────────────────────────────
                    item {
                        Text(
                            text = "Riwayat Pesanan",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    // ── Filter chip ───────────────────────────
//                    item {
//                        Row(
//                            modifier = Modifier
//                                .horizontalScroll(rememberScrollState()),
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            categories.forEach { cat ->
//                                val selected = cat == uiState.selectedCategory
//                                FilterChip(
//                                    selected = selected,
//                                    onClick = { viewModel.filterByCategory(cat) },
//                                    label = { Text(cat, fontSize = 13.sp) },
//                                    colors = FilterChipDefaults.filterChipColors(
//                                        selectedContainerColor = GreenDark,
//                                        selectedLabelColor = Color.White,
//                                        containerColor = Color.White,
//                                        labelColor = TextSecondary
//                                    ),
//                                    border = FilterChipDefaults.filterChipBorder(
//                                        enabled = true,
//                                        selected = selected,
//                                        borderColor = Color(0xFFE0E0E0),
//                                        selectedBorderColor = GreenDark
//                                    )
//                                )
//                            }
//                        }
//                        Spacer(Modifier.height(4.dp))
//                    }

                    // ── Empty state ───────────────────────────
                    if (uiState.filteredOrders.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Belum ada pesanan",
                                    color = TextSecondary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // ── List order ────────────────────────────
                    items(uiState.filteredOrders) { order ->
                        OrderCard(order = order)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// OrderCard
// ─────────────────────────────────────────────────────────────
@Composable
private fun OrderCard(order: OrderWithProduct) {
    val product = order.products

    // Format tanggal dari createdAt (ISO 8601 → "12 Okt 2023")
    val formattedDate = remember(order.createdAt) {
        formatOrderDate(order.createdAt)
    }

    // Format harga
    val formattedPrice = remember(order.totalPrice) {
        "Rp " + order.totalPrice.toString()
            .reversed().chunked(3).joinToString(".").reversed()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Tanggal
            Text(
                text = formattedDate,
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(Modifier.height(10.dp))

            // Foto + info produk
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Foto
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF0F0F0))
                ) {
                    AsyncImage(
                        model = product?.thumbnailUrl,
                        contentDescription = product?.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Nama + badge kategori
                Column {
                    Text(
                        text = product?.name ?: "-",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(4.dp))
                    if (!product?.category.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(GreenChip)
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = product!!.category,
                                fontSize = 11.sp,
                                color = GreenDark,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            HorizontalDivider(color = Color(0xFFF0F0F0))

            Spacer(Modifier.height(10.dp))

            // Quantity (kiri) + Total harga (kanan)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${order.quantity} barang",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Text(
                    text = formattedPrice,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenDark
                )
            }
        }
    }
}

// Format ISO date → "12 Okt 2023"
private fun formatOrderDate(createdAt: String?): String {
    if (createdAt.isNullOrBlank()) return "-"
    return try {
        val months = listOf(
            "Jan","Feb","Mar","Apr","Mei","Jun",
            "Jul","Agt","Sep","Okt","Nov","Des"
        )
        val date = createdAt.substring(0, 10) // "2023-10-12"
        val parts = date.split("-")
        val day   = parts[2].trimStart('0')
        val month = months[parts[1].toInt() - 1]
        val year  = parts[0]
        "$day $month $year"
    } catch (e: Exception) {
        createdAt.substring(0, 10)
    }
}