package com.example.lokamart.ui.screen.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Restore
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.lokamart.data.model.Product
import com.example.lokamart.data.model.thumbnailUrl
import com.example.lokamart.ui.components.BottomBar.LokaMartHeaderWithBack
import com.example.lokamart.ui.navigation.Screen
import com.example.lokamart.ui.screen.auth.GreenDark
import com.example.lokamart.ui.viewmodel.ManageProductsViewModel

private val RedLight = Color(0xFFFFEBEE)
private val RedDark = Color(0xFFD32F2F)
private val OrangeDark = Color(0xFFE65100)
private val OrangeLight = Color(0xFFFFF3E0)
private val TextSecondary = Color(0xFF757575)

@Composable
fun ManageProductScreen(
    navController: NavController,
    viewModel: ManageProductsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {

        Surface(color = Color.White, shadowElevation = 2.dp) {
            LokaMartHeaderWithBack(onBack = { navController.navigateUp() })
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Kelola Produk",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenDark
                    )
                    Text(
                        text = "Kelola inventaris toko Anda dengan mudah dan cepat.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                IconButton(onClick = { navController.navigate(Screen.CreateProduct.route) }) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Tambah Produk",
                        tint = GreenDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearch(it) },
                placeholder = { Text("Cari nama produk...", color = TextSecondary, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenDark,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = GreenDark,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Aktif",
                            fontWeight = if (selectedTab == 0) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Diarsipkan",
                            fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GreenDark)
                    }
                }

                uiState.errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = uiState.errorMessage ?: "Terjadi kesalahan",
                            color = RedDark,
                            fontSize = 14.sp
                        )
                    }
                }

                else -> {
                    val displayList = if (selectedTab == 0) {
                        uiState.filteredProducts.filter { !it.isArchived }
                    } else {
                        uiState.filteredProducts.filter { it.isArchived }
                    }

                    if (displayList.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (selectedTab == 0) "Belum ada produk aktif." else "Belum ada produk diarsipkan.",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(displayList) { product ->
                                ProductItem(
                                    product = product,
                                    isArchived = product.isArchived,
                                    onEdit = {
                                        navController.navigate(Screen.EditProduct.createRoute(product.id))
                                    },
                                    onArchive = { viewModel.archiveProduct(product.id) },
                                    onUnarchive = { viewModel.unarchiveProduct(product.id) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductItem(
    product: Product,
    isArchived: Boolean,
    onEdit: () -> Unit,
    onArchive: () -> Unit,
    onUnarchive: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (isArchived) "Pulihkan Produk" else "Arsipkan Produk") },
            text = {
                Text(
                    if (isArchived)
                        "Produk \"${product.name}\" akan dipulihkan ke daftar aktif. Lanjutkan?"
                    else
                        "Produk \"${product.name}\" akan diarsipkan. Lanjutkan?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    if (isArchived) onUnarchive() else onArchive()
                }) {
                    Text("Ya", color = if (isArchived) GreenDark else RedDark)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.thumbnailUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rp ${"%,d".format(product.price).replace(',', '.')}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Stok: ${product.stock} unit",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    if (product.stock in 1..5) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(OrangeLight, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "STOK RENDAH",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangeDark
                            )
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (!isArchived) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit",
                            tint = GreenDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                IconButton(onClick = { showDialog = true }, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (isArchived) Icons.Outlined.Restore else Icons.Outlined.Delete,
                        contentDescription = if (isArchived) "Pulihkan" else "Arsipkan",
                        tint = if (isArchived) GreenDark else RedDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}