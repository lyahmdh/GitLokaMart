package com.example.lokamart.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokamart.data.model.Favorite
import com.example.lokamart.data.repository.ProductRepository
import com.example.lokamart.data.repository.FavoriteRepository
import com.example.lokamart.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = ProductRepository()
    private val authRepository = AuthRepository()
    private val favoriteRepository = FavoriteRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
        loadFavorites()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.getProducts()
                .onSuccess { products ->
                    _uiState.update {
                        it.copy(isLoading = false, products = products)
                    }
                    applyFilter()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message)
                    }
                }
        }
    }

    fun updateSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilter()
    }

    fun updateCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        applyFilter()
    }

    fun updateSort(sort: String) {
        _uiState.update { it.copy(selectedSort = sort) }
        applyFilter()
    }

    private fun applyFilter() {
        val state = _uiState.value
        var result = state.products

        // SEARCH
        if (state.searchQuery.isNotBlank()) {
            result = result.filter {
                it.name.contains(
                    state.searchQuery,
                    ignoreCase = true
                )
            }
        }

        // CATEGORY
        if (state.selectedCategory != "Semua") {
            result = result.filter {
                it.category == state.selectedCategory
            }
        }

        // SORT
        result = when (state.selectedSort) {

            "Harga Terendah" -> {
                result.sortedBy { it.price }
            }

            "Harga Tertinggi" -> {
                result.sortedByDescending { it.price }
            }

            else -> {
                result.sortedByDescending { it.createdAt }
            }
        }

        _uiState.update {
            it.copy(filteredProducts = result)
        }
    }

    // ── Favorites ─────────────────────────────────────────────
    private fun loadFavorites() {
        val user = authRepository.getCurrentUser()
        Log.d("FAV_DEBUG", "user = $user")

        if (user == null) {
            Log.d("FAV_DEBUG", "USER NULL → STOP")
            return
        }

        viewModelScope.launch {
            favoriteRepository.getMyFavorites(user.id)
                .onSuccess { list ->
                    Log.d("FAV_DEBUG", "favorites size = ${list.size}")
                    Log.d("FAV_DEBUG", "raw favorites = $list")

                    _uiState.update {
                        it.copy(
                            favoriteIds = list.map { f -> f.productId }.toSet()
                        )
                    }
                }
                .onFailure {
                    Log.e("FAV_DEBUG", "FAILED LOAD FAVORITES", it)
                }
        }
    }

    fun toggleFavorite(productId: String) {
        val user = authRepository.getCurrentUser() ?: return

        Log.d("FAV_DEBUG", "CLICK FAVORITE productId = $productId")

        viewModelScope.launch {

            if (productId in _uiState.value.favoriteIds) {

                favoriteRepository.removeFavorite(user.id, productId)
                    .onSuccess {
                        Log.d("FAV_DEBUG", "REMOVE SUCCESS")
                    }
                    .onFailure {
                        Log.e("FAV_DEBUG", "REMOVE FAILED", it)
                    }

            } else {

                favoriteRepository.addFavorite(
                    Favorite(
                        userId = user.id,
                        productId = productId
                    )
                )
                    .onSuccess {
                        Log.d("FAV_DEBUG", "INSERT SUCCESS")
                    }
                    .onFailure {
                        Log.e("FAV_DEBUG", "INSERT FAILED", it)
                    }
            }

            loadFavorites()
        }
    }
}
