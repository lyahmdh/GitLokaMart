package com.example.lokamart.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokamart.data.model.Product
import com.example.lokamart.data.repository.AuthRepository
import com.example.lokamart.data.repository.FavoriteRepository
import com.example.lokamart.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoriteViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val favoriteRepository = FavoriteRepository()
    private val productRepository = ProductRepository()

    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    // Simpan semua produk favorit
    private var allFavoriteProducts: List<Product> = emptyList()

    init {
        loadFavoriteProducts()
    }

    fun loadFavoriteProducts() {
        val user = authRepository.getCurrentUser() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Ambil daftar favorit (product_id saja)
            favoriteRepository.getMyFavorites(user.id)
                .onSuccess { favorites ->
                    val productIds = favorites.map { it.productId }

                    if (productIds.isEmpty()) {
                        allFavoriteProducts = emptyList()
                        _uiState.update {
                            it.copy(isLoading = false, favoriteProducts = emptyList())
                        }
                        return@onSuccess
                    }

                    // Ambil semua produk lalu filter berdasarkan productIds
                    productRepository.getProducts()
                        .onSuccess { products ->
                            allFavoriteProducts = products.filter { it.id in productIds }
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    favoriteProducts = applyCategory(
                                        allFavoriteProducts,
                                        it.selectedCategory
                                    )
                                )
                            }
                        }
                        .onFailure { e ->
                            _uiState.update {
                                it.copy(isLoading = false, errorMessage = e.message)
                            }
                        }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message)
                    }
                }
        }
    }

    fun updateCategory(category: String) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                favoriteProducts = applyCategory(allFavoriteProducts, category)
            )
        }
    }

    private fun applyCategory(products: List<Product>, category: String): List<Product> {
        return if (category == "Semua") products
        else products.filter { it.category == category }
    }
}