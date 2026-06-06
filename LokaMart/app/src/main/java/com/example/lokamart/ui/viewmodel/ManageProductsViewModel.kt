package com.example.lokamart.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokamart.data.model.Product
import com.example.lokamart.data.repository.AuthRepository
import com.example.lokamart.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import java.time.Instant

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
        imageUri: Uri? = null
    ) {
        val user = authRepository.getCurrentUser() ?: return
        val now = Instant.now().toString()

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
                    if (imageUri != null) {
                        try {
                            val inputStream = context.contentResolver.openInputStream(imageUri)
                            val bytes = inputStream?.readBytes()
                            inputStream?.close()

                            if (bytes != null) {
                                repository.uploadProductImage(bytes)
                                    .onSuccess { imageUrl ->
                                        repository.insertProductImage(createdProduct.id, imageUrl)
                                            .onSuccess {
                                                _uiState.update { it.copy(isLoading = false, createSuccess = true) }
                                                loadMyProducts()
                                            }
                                            .onFailure { e ->
                                                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                                            }
                                    }
                                    .onFailure { e ->
                                        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                                    }
                            } else {
                                _uiState.update { it.copy(isLoading = false, errorMessage = "File error") }
                            }
                        } catch (e: Exception) {
                            _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                        }
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
}