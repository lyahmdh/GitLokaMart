package com.example.lokamart.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokamart.data.model.Favorite
import com.example.lokamart.data.model.Order
import com.example.lokamart.data.repository.AuthRepository
import com.example.lokamart.data.repository.FavoriteRepository
import com.example.lokamart.data.repository.OrderRepository
import com.example.lokamart.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ProductDetailViewModel : ViewModel() {

    private val productRepository = ProductRepository()
    private val favoriteRepository = FavoriteRepository()
    private val orderRepository = OrderRepository()
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            productRepository.getProductById(productId)
                .onSuccess { product ->
                    _uiState.update { it.copy(isLoading = false, product = product) }
                    loadRelatedProducts(product.category, productId)
                    checkFavorite(productId)
                    loadSellerProfile(product.userId)
                }
                .onFailure { e ->
                    Log.e("ProductDetailVM", "Error loading product: ${e.message}", e)
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message)
                    }
                }
        }
    }

    private fun loadRelatedProducts(
        category: String,
        excludeId: String
    ) {
        viewModelScope.launch {

            productRepository.getProducts()
                .onSuccess { products ->

                    val related = products
                        .filter {
                            it.category.trim().lowercase() ==
                                    category.trim().lowercase()
                        }
                        .filter {
                            it.id != excludeId
                        }
                        .filter {
                            !it.isArchived
                        }
                        .shuffled()
                        .take(6)

                    _uiState.update {
                        it.copy(
                            relatedProducts = related
                        )
                    }
                }
        }
    }
    private fun loadSellerProfile(userId: String) {
        viewModelScope.launch {
            authRepository.getProfile(userId)
                .onSuccess { profile ->
                    _uiState.update { it.copy(sellerProfile = profile) }
                }
                .onFailure { e ->
                    Log.e("ProductDetailVM", "Error loading seller: ${e.message}", e)
                }
        }
    }
    private fun checkFavorite(productId: String) {
        val user = authRepository.getCurrentUser() ?: return
        viewModelScope.launch {
            favoriteRepository.getMyFavorites(user.id)
                .onSuccess { favorites ->
                    val isFav = favorites.any { it.productId == productId }
                    _uiState.update { it.copy(isFavorite = isFav) }
                }
                .onFailure { e ->
                    Log.e("ProductDetailVM", "Error checking favorite: ${e.message}", e)
                }
        }
    }

    fun toggleFavorite() {
        val user = authRepository.getCurrentUser() ?: return
        val productId = _uiState.value.product?.id ?: return

        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                favoriteRepository.removeFavorite(user.id, productId)
                    .onSuccess {
                        _uiState.update { it.copy(isFavorite = false) }
                        Log.d("ProductDetailVM", "Removed from favorites")
                    }
                    .onFailure { e ->
                        Log.e("ProductDetailVM", "Error removing favorite: ${e.message}", e)
                    }
            } else {
                favoriteRepository.addFavorite(
                    Favorite(userId = user.id, productId = productId)
                )
                    .onSuccess {
                        _uiState.update { it.copy(isFavorite = true) }
                        Log.d("ProductDetailVM", "Added to favorites")
                    }
                    .onFailure { e ->
                        Log.e("ProductDetailVM", "Error adding favorite: ${e.message}", e)
                    }
            }
        }
    }

    fun placeOrder(quantity: Int) {
        val user = authRepository.getCurrentUser() ?: return
        val product = _uiState.value.product ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isOrdering = true) }

            val order = Order(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                productId = product.id,
                quantity = quantity,
                totalPrice = product.price * quantity
            )

            orderRepository.createOrder(order)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isOrdering = false,
                            orderSuccess = true
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isOrdering = false,
                            errorMessage = e.message
                        )
                    }
                }
        }
    }

    fun clearOrderSuccess() {
        _uiState.update { it.copy(orderSuccess = false) }
    }
}