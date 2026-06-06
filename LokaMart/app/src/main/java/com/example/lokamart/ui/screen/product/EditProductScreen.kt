package com.example.lokamart.ui.screen.product

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.lokamart.data.model.thumbnailUrl
import com.example.lokamart.ui.components.BottomBar.LokaMartHeaderWithBack
import com.example.lokamart.ui.screen.auth.GreenDark
import com.example.lokamart.ui.viewmodel.ManageProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    productId: String,
    navController: NavController,
    viewModel: ManageProductsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val product = remember(uiState.products) {
        uiState.products.firstOrNull { it.id == productId }
    }

    var namaProduct by remember(product) { mutableStateOf(product?.name ?: "") }
    var kategori by remember(product) { mutableStateOf(product?.category ?: "") }
    var harga by remember(product) { mutableStateOf(product?.price?.toString() ?: "") }
    var deskripsi by remember(product) { mutableStateOf(product?.description ?: "") }
    var stok by remember(product) { mutableIntStateOf(product?.stock ?: 1) }
    var kategoriExpanded by remember { mutableStateOf(false) }

    val kategoriList = listOf("Kerajinan Tangan", "Fashion", "Peralatan Rumah Tangga", "Dekorasi Ruangan")
    val TextSecondary = Color(0xFF757575)
    val BorderColor = Color(0xFFE0E0E0)

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            viewModel.resetUpdateSuccess()
            navController.navigateUp()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .statusBarsPadding()
    ){

        Surface(color = Color.White, shadowElevation = 2.dp) {
            LokaMartHeaderWithBack(onBack = { navController.navigateUp() })
        }

        if (product == null && !uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Produk tidak ditemukan.", color = TextSecondary)
            }
            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Edit Produk",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = GreenDark
            )
            Text(
                text = "Perbarui informasi produk Anda.",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            product?.thumbnailUrl?.let { url ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = url,
                        contentDescription = "Foto produk",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text("NAMA PRODUK", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = namaProduct,
                onValueChange = { namaProduct = it },
                placeholder = { Text("Nama produk", color = TextSecondary, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenDark,
                    unfocusedBorderColor = BorderColor
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("KATEGORI", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = kategoriExpanded,
                        onExpandedChange = { kategoriExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = kategori.ifBlank { "Pilih Kategori" },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = kategoriExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenDark,
                                unfocusedBorderColor = BorderColor
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = kategoriExpanded,
                            onDismissRequest = { kategoriExpanded = false }
                        ) {
                            kategoriList.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        kategori = item
                                        kategoriExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("HARGA (RP)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = harga,
                        onValueChange = { harga = it.filter { c -> c.isDigit() } },
                        prefix = { Text("Rp ", color = GreenDark, fontWeight = FontWeight.Medium) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenDark,
                            unfocusedBorderColor = BorderColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("DESKRIPSI PRODUK", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = deskripsi,
                onValueChange = { deskripsi = it },
                placeholder = { Text("Deskripsi produk...", color = TextSecondary, fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(10.dp),
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenDark,
                    unfocusedBorderColor = BorderColor
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("STOK PRODUK", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = { if (stok > 0) stok-- },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenDark)
                ) {
                    Text("−", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = stok.toString(),
                    modifier = Modifier.padding(horizontal = 20.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                OutlinedButton(
                    onClick = { stok++ },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenDark)
                ) {
                    Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage ?: "",
                    color = Color(0xFFD32F2F),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenDark)
                ) {
                    Text("Batal", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        product?.let {
                            viewModel.updateProduct(
                                product = it.copy(
                                    name = namaProduct,
                                    category = kategori,
                                    price = harga.toIntOrNull() ?: it.price,
                                    description = deskripsi,
                                    stock = stok
                                )
                            )
                        }
                    },
                    enabled = !uiState.isLoading && namaProduct.isNotBlank() && kategori.isNotBlank(),
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenDark)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Outlined.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}