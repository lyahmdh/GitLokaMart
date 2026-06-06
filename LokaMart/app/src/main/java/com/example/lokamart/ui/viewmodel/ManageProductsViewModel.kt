package com.example.lokamart.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokamart.data.model.Product
import com.example.lokamart.data.model.ProductImage
import com.example.lokamart.data.repository.AuthRepository
import com.example.lokamart.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class ManageProductsUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null,
    val createSuccess: Boolean = false,
    val updateSuccess: Boolean = false
)

class ManageProductsViewModel : ViewModel() {

    private val repository = ProductRepository()
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(ManageProductsUiState())
    val uiState: StateFlow<ManageProductsUiState> = _uiState.asStateFlow()

    init {
        loadMyProducts()
    }

    fun loadMyProducts() {
        val user = authRepository.getCurrentUser() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getMyProducts(user.id)
                .onSuccess { list ->
                    _uiState.update { it.copy(isLoading = false, products = list, filteredProducts = list) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }

    fun updateSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilter()
    }

    private fun applyFilter() {
        val state = _uiState.value
        val result = if (state.searchQuery.isBlank()) {
            state.products
        } else {
            state.products.filter { it.name.contains(state.searchQuery, ignoreCase = true) }
        }
        _uiState.update { it.copy(filteredProducts = result) }
    }

    fun createProduct(
        context: Context,
        name: String,
        category: String,
        price: Int,
        description: String,
        stock: Int,
        imageUris: List<Uri> = emptyList()
    ) {
        val user = authRepository.getCurrentUser() ?: return
        val now = java.text.SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            java.util.Locale.getDefault()
        ).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }.format(java.util.Date())


        val newProduct = Product(
            id = UUID.randomUUID().toString(),
            userId = user.id,
            name = name,
            category = category,
            price = price,
            description = description,
            stock = stock,
            createdAt = now
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.createProduct(newProduct)
                .onSuccess { createdProduct ->
                    if (imageUris.isNotEmpty()) {
                        val uploadErrors = mutableListOf<String>()

                        imageUris.forEachIndexed { index, uri ->
                            try {
                                val inputStream = context.contentResolver.openInputStream(uri)
                                val bytes = inputStream?.readBytes()
                                inputStream?.close()

                                if (bytes != null) {
                                    val mimeType = context.contentResolver.getType(uri)
                                    val ext = when (mimeType) {
                                        "image/png" -> "png"
                                        "image/webp" -> "webp"
                                        else -> "jpg"
                                    }

                                    repository.uploadProductImage(bytes, ext)
                                        .onSuccess { imageUrl ->
                                            repository.insertProductImage(
                                                productId = createdProduct.id,
                                                imageUrl = imageUrl,
                                                isThumbnail = (index == 0),
                                                sortOrder = index
                                            ).onFailure { e ->
                                                uploadErrors.add(e.message ?: "Insert error")
                                            }
                                        }
                                        .onFailure { e ->
                                            uploadErrors.add(e.message ?: "Upload error")
                                        }
                                } else {
                                    uploadErrors.add("File kosong pada gambar ke-${index + 1}")
                                }
                            } catch (e: Exception) {
                                uploadErrors.add(e.message ?: "Exception")
                            }
                        }

                        if (uploadErrors.isEmpty()) {
                            _uiState.update { it.copy(isLoading = false, createSuccess = true) }
                        } else {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    createSuccess = true,
                                    errorMessage = "Produk disimpan, tapi ada error upload gambar: ${uploadErrors.first()}"
                                )
                            }
                        }
                        loadMyProducts()
                    } else {
                        _uiState.update { it.copy(isLoading = false, createSuccess = true) }
                        loadMyProducts()
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.updateProduct(product)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, updateSuccess = true) }
                    loadMyProducts()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }

    fun updateProductWithImages(
        context: Context,
        product: Product,
        imagesToDelete: List<ProductImage>,
        newImageUris: List<Uri>,
        existingImages: List<ProductImage>
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.updateProduct(product)
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                    return@launch
                }

            imagesToDelete.forEach { img ->
                repository.deleteProductImageById(img.id)
                repository.deleteImageFromStorage(img.imageUrl)
            }

            val uploadErrors = mutableListOf<String>()
            val uploadedImages = mutableListOf<String>()

            newImageUris.forEach { uri ->
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (bytes != null) {
                        val mimeType = context.contentResolver.getType(uri)
                        val ext = when (mimeType) {
                            "image/png" -> "png"
                            "image/webp" -> "webp"
                            else -> "jpg"
                        }

                        repository.uploadProductImage(bytes, ext)
                            .onSuccess { imageUrl -> uploadedImages.add(imageUrl) }
                            .onFailure { e -> uploadErrors.add(e.message ?: "Upload error") }
                    }
                } catch (e: Exception) {
                    uploadErrors.add(e.message ?: "Exception")
                }
            }

            val baseOrder = existingImages.size
            uploadedImages.forEachIndexed { index, imageUrl ->
                val hasExistingThumbnail = existingImages.any { it.isThumbnail }
                val isThumbnail = !hasExistingThumbnail && index == 0

                repository.insertProductImage(
                    productId = product.id,
                    imageUrl = imageUrl,
                    isThumbnail = isThumbnail,
                    sortOrder = baseOrder + index
                )
            }

            if (uploadErrors.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, updateSuccess = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        updateSuccess = true,
                        errorMessage = "Perubahan disimpan, tapi ada error upload: ${uploadErrors.first()}"
                    )
                }
            }
            loadMyProducts()
        }
    }

    fun deleteProductImage(image: ProductImage) {
        viewModelScope.launch {
            repository.deleteProductImageById(image.id)
            repository.deleteImageFromStorage(image.imageUrl)
            loadMyProducts()
        }
    }

    fun archiveProduct(productId: String) {
        viewModelScope.launch {
            repository.archiveProduct(productId)
                .onSuccess { loadMyProducts() }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
        }
    }

    fun unarchiveProduct(productId: String) {
        viewModelScope.launch {
            repository.unarchiveProduct(productId)
                .onSuccess { loadMyProducts() }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
        }
    }

    fun resetCreateSuccess() {
        _uiState.update { it.copy(createSuccess = false) }
    }

    fun resetUpdateSuccess() {
        _uiState.update { it.copy(updateSuccess = false) }
    }
    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
                .onSuccess { loadMyProducts() }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
        }
    }

}