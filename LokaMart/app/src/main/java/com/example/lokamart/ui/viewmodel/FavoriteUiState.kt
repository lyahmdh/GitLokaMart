package com.example.lokamart.ui.viewmodel

import com.example.lokamart.data.model.Product

data class FavoriteUiState(
    val isLoading: Boolean = false,
    val favoriteProducts: List<Product> = emptyList(),
    val selectedCategory: String = "Semua",
    val errorMessage: String? = null
)