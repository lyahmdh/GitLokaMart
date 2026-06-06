package com.example.lokamart.ui.screen.product

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.lokamart.data.model.ProductImage
import com.example.lokamart.data.model.thumbnailUrl
import com.example.lokamart.ui.components.BottomBar.LokaMartHeaderWithBack
import com.example.lokamart.ui.screen.auth.GreenDark
import com.example.lokamart.ui.viewmodel.ManageProductsViewModel

private const val EDIT_MAX_IMAGES = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    productId: String,
    navController: NavController,
    viewModel: ManageProductsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val TextSecondary = Color(0xFF757575)
    val BorderColor = Color(0xFFE0E0E0)

    val product = remember(uiState.products) {
        uiState.products.firstOrNull { it.id == productId }
    }

    var namaProduct by remember(product) { mutableStateOf(product?.name ?: "") }
    var kategori by remember(product) { mutableStateOf(product?.category ?: "") }
    var harga by remember(product) { mutableStateOf(product?.price?.toString() ?: "") }
    var deskripsi by remember(product) { mutableStateOf(product?.description ?: "") }
    var stok by remember(product) { mutableIntStateOf(product?.stock ?: 1) }
    var kategoriExpanded by remember { mutableStateOf(false) }

    // Gambar existing yang masih dipertahankan user (belum dihapus)
    var existingImages by remember(product) {
        mutableStateOf<List<ProductImage>>(product?.productImages ?: emptyList())
    }
    // Gambar yang user tandai untuk dihapus
    var imagesToDelete by remember { mutableStateOf<List<ProductImage>>(emptyList()) }
    // Uri gambar baru yang dipilih user
    var newImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val totalImages = existingImages.size + newImageUris.size

    val kategoriList = listOf("Kerajinan Tangan", "Fashion", "Peralatan Rumah Tangga", "Dekorasi Ruangan")

    // Launcher multi-dokumen untuk tambah gambar baru
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val remaining = EDIT_MAX_IMAGES - totalImages
            val toAdd = uris.take(remaining)
            newImageUris = newImageUris + toAdd
        }
    }

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
                text = "Perbarui informasi dan foto produk Anda.",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // === SECTION FOTO ===
            Text(
                text = "Foto Produk",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = GreenDark
            )
            Text(
                text = "Maks. $EDIT_MAX_IMAGES foto. Tekan ✕ untuk hapus foto.",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Preview gambar pertama (existing atau baru)
            val previewUri: Any? = when {
                existingImages.isNotEmpty() -> existingImages.first().imageUrl
                newImageUris.isNotEmpty() -> newImageUris.first()
                else -> null
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.5.dp, BorderColor, RoundedCornerShape(12.dp))
                    .background(Color(0xFFF9F9F9))
                    .clickable {
                        if (totalImages < EDIT_MAX_IMAGES) {
                            imagePickerLauncher.launch(arrayOf("image/*"))
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (previewUri != null) {
                    AsyncImage(
                        model = previewUri,
                        contentDescription = "Foto utama produk",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(GreenDark.copy(alpha = 0.85f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Thumbnail", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AddPhotoAlternate,
                                contentDescription = "Tambah Foto",
                                tint = GreenDark,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tambah Foto", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = GreenDark)
                        Text("Maks. $EDIT_MAX_IMAGES foto (JPG, PNG)", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row thumbnail — existing + baru
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                // Gambar existing (dari Supabase)
                itemsIndexed(existingImages) { index, img ->
                    Box(modifier = Modifier.size(64.dp)) {
                        AsyncImage(
                            model = img.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    1.5.dp,
                                    if (img.isThumbnail) GreenDark else BorderColor,
                                    RoundedCornerShape(8.dp)
                                ),
                            contentScale = ContentScale.Crop
                        )
                        // Badge thumbnail
                        if (img.isThumbnail) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(GreenDark.copy(alpha = 0.85f))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("★", fontSize = 9.sp, color = Color.White)
                            }
                        }
                        // Tombol hapus
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.TopEnd)
                                .clip(CircleShape)
                                .background(Color(0xFFD32F2F))
                                .clickable {
                                    imagesToDelete = imagesToDelete + img
                                    existingImages = existingImages.toMutableList().also { it.removeAt(index) }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Hapus",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Gambar baru (dari picker)
                itemsIndexed(newImageUris) { index, uri ->
                    Box(modifier = Modifier.size(64.dp)) {
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.5.dp, Color(0xFF1976D2), RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        // Badge "baru"
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF1976D2).copy(alpha = 0.85f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("Baru", fontSize = 9.sp, color = Color.White)
                        }
                        // Tombol hapus
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.TopEnd)
                                .clip(CircleShape)
                                .background(Color(0xFFD32F2F))
                                .clickable {
                                    newImageUris = newImageUris.toMutableList().also { it.removeAt(index) }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Hapus",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Slot tambah gambar baru
                if (totalImages < EDIT_MAX_IMAGES) {
                    item {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                                .background(Color(0xFFF9F9F9))
                                .clickable { imagePickerLauncher.launch(arrayOf("image/*")) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Outlined.Image,
                                    contentDescription = "Tambah",
                                    tint = GreenDark,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    "+${EDIT_MAX_IMAGES - totalImages}",
                                    fontSize = 10.sp,
                                    color = GreenDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = "$totalImages/$EDIT_MAX_IMAGES foto" +
                        if (imagesToDelete.isNotEmpty()) " · ${imagesToDelete.size} akan dihapus" else "",
                fontSize = 11.sp,
                color = if (imagesToDelete.isNotEmpty()) Color(0xFFD32F2F) else TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = BorderColor)
            Spacer(modifier = Modifier.height(16.dp))

            // === SECTION INFO PRODUK ===
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
                            val updatedProduct = it.copy(
                                name = namaProduct,
                                category = kategori,
                                price = harga.toIntOrNull() ?: it.price,
                                description = deskripsi,
                                stock = stok
                            )
                            viewModel.updateProductWithImages(
                                context = context,
                                product = updatedProduct,
                                imagesToDelete = imagesToDelete,
                                newImageUris = newImageUris,
                                existingImages = existingImages
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
