package com.example.lokamart.ui.screen.product

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.lokamart.data.model.Product
import com.example.lokamart.data.model.thumbnailUrl
import com.example.lokamart.ui.screen.auth.GreenDark
import com.example.lokamart.ui.viewmodel.ProductDetailViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material.icons.outlined.CheckCircle

// ── Local colors ─────────────────────────────────────────────
private val GreenChip     = Color(0xFFDCEFDC)
private val TextPrimary   = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF757575)
private val DividerColor  = Color(0xFFF0F0F0)

// ─────────────────────────────────────────────────────────────
// ProductDetailScreen
// ─────────────────────────────────────────────────────────────
@Composable
fun ProductDetailScreen(
    productId: String,
    navController: NavController,
    viewModel: ProductDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    var quantity by remember {
        mutableIntStateOf(0)
    }
    var showQuantityError by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.orderSuccess) {
        if (uiState.orderSuccess) {
            showSuccessDialog = true
            viewModel.clearOrderSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { innerPadding ->

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GreenDark)
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Gagal memuat produk", color = TextSecondary, fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.loadProduct(productId) },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenDark)
                        ) { Text("Coba Lagi") }
                    }
                }
            }

            uiState.product != null -> {
                val product = uiState.product!!

                // State untuk gambar utama yang sedang ditampilkan
                val sortedImages = product.productImages.sortedBy { it.sortOrder }
                var selectedImageUrl by remember(product.id) {
                    mutableStateOf(
                        sortedImages.firstOrNull { it.isThumbnail }?.imageUrl
                            ?: sortedImages.firstOrNull()?.imageUrl
                    )
                }

                if (showSuccessDialog) {
                    AlertDialog(
                        onDismissRequest = { showSuccessDialog = false },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = GreenDark,
                                modifier = Modifier.size(48.dp)
                            )
                        },
                        title = {
                            Text(
                                text = "Pesanan Berhasil!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        },
                        text = {
                            Text(
                                text = "Barang berhasil ditambahkan ke pesanan kamu.",
                                fontSize = 14.sp,
                                color = Color(0xFF757575)
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = { showSuccessDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = GreenDark),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Oke")
                            }
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {

                    // ── Top Bar ───────────────────────────────────────
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Kembali",
                                tint = GreenDark,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { navController.navigateUp() }
                            )
                            Text(
                                text = "LokaMart",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenDark
                            )
                        }
                    }

                    // ── Gambar Utama ──────────────────────────────────
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .height(280.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(DividerColor)
                        ) {
                            if (selectedImageUrl != null) {
                                AsyncImage(
                                    model = selectedImageUrl,
                                    contentDescription = product.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                // Placeholder jika belum ada gambar
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Tidak ada gambar",
                                        color = TextSecondary,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    // ── Thumbnail Gallery ─────────────────────────────
                    item {
                        val visibleCount = 3
                        val extraCount = (sortedImages.size - visibleCount).coerceAtLeast(0)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            sortedImages.take(visibleCount).forEach { image ->
                                val isSelected = image.imageUrl == selectedImageUrl
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) GreenDark else Color(0xFFE0E0E0),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .background(DividerColor)
                                        .clickable { selectedImageUrl = image.imageUrl }
                                ) {
                                    AsyncImage(
                                        model = image.imageUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            // "+N" box
                            if (extraCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            width = 1.dp,
                                            color = Color(0xFFE0E0E0),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .background(Color(0xFFF5F5F5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+$extraCount",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // ── Badge Kategori + Nama + Deskripsi ─────────────
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            // Badge kategori
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(GreenChip)
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = product.category,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = GreenDark
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                            // Nama produk
                            Text(
                                text = product.name,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                lineHeight = 24.sp
                            )

                            Spacer(Modifier.height(12.dp))

                            // Judul deskripsi
                            Text(
                                text = "Deskripsi Produk",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Spacer(Modifier.height(4.dp))

                            // Isi deskripsi
                            Text(
                                text = if (!product.description.isNullOrBlank())
                                    product.description
                                else
                                    "Tidak ada deskripsi.",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                lineHeight = 22.sp
                            )

                            Spacer(Modifier.height(16.dp))
                        }
                    }

                    // ── Kartu Penjual ─────────────────────────────────────────
                    item {
                        val seller = uiState.sellerProfile

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(GreenDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Person,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = seller?.name?.ifBlank { "Penjual" } ?: "Penjual",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.LocationOn,
                                            contentDescription = null,
                                            tint = TextSecondary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = seller?.location?.ifBlank { "Indonesia" } ?: "Indonesia",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    item {
                        Text(
                            text = "Jumlah Barang",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (quantity > 0) {
                                        quantity--
                                        showQuantityError = false  // ← reset error saat dikurangi
                                    }
                                }
                            ) { Text("-") }

                            Text(
                                text = quantity.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedButton(
                                onClick = {
                                    quantity++
                                    showQuantityError = false  // ← reset error saat ditambah
                                }
                            ) { Text("+") }
                        }

                        // Pesan error quantity
                        if (showQuantityError) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Masukkan jumlah barang terlebih dahulu",
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // ── Tombol Beli + Favorit ─────────────────────────
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Button(
                                onClick = {
                                    if (quantity == 0) {
                                        showQuantityError = true   // ← tampilkan error
                                    } else {
                                        showQuantityError = false
                                        viewModel.placeOrder(quantity)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (quantity == 0) Color(0xFFB0B0B0) else GreenDark  // ← abu-abu kalau 0
                                ),
                                enabled = !uiState.isOrdering
                            ) {
                                if (uiState.isOrdering) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = "Beli Sekarang",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFFE0E0E0),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .background(Color.White)
                                    .clickable {
                                        viewModel.toggleFavorite()
                                    },
                                contentAlignment = Alignment.Center
                            ) {

                                Icon(
                                    imageVector =
                                        if (uiState.isFavorite)
                                            Icons.Outlined.Favorite
                                        else
                                            Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Favorit",
                                    tint =
                                        if (uiState.isFavorite)
                                            Color.Red
                                        else
                                            Color(0xFF757575),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // ── Section: Rekomendasi Produk ───────────────────
                    if (uiState.relatedProducts.isNotEmpty()) {

                        item {
                            Text(
                                text = "Produk Lainnya",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 4.dp,
                                    bottom = 8.dp
                                )
                            )
                        }

                        items(
                            uiState.relatedProducts.chunked(2)
                        ) { rowProducts ->

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                rowProducts.forEach { product ->

                                    RelatedProductCard(
                                        product = product,
                                        onClick = {
                                            navController.navigate(
                                                "product_detail/${product.id}"
                                            )
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                if (rowProducts.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// RelatedProductCard
// ─────────────────────────────────────────────────────────────
@Composable
private fun RelatedProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            // Foto produk
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(DividerColor)
            ) {
                AsyncImage(
                    model = product.thumbnailUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(modifier = Modifier.padding(10.dp)) {
                // Nama
                Text(
                    text = product.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                Spacer(Modifier.height(4.dp))

                // Rating (placeholder statis, tidak ada field rating di model)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(text = "5.0", fontSize = 11.sp, color = TextSecondary)
                }

                Spacer(Modifier.height(4.dp))

                // Harga
                Text(
                    text = "Rp ${"${product.price}".reversed()
                        .chunked(3).joinToString(".").reversed()}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenDark
                )

                Spacer(Modifier.height(4.dp))

                // Toko
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Store,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = "Toko UMKM",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}