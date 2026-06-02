package com.example.lokamart.ui.viewmodel

import com.example.lokamart.data.model.Product

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val relatedProducts: List<Product> = emptyList(),
    val isFavorite: Boolean = false,
    val errorMessage: String? = null,
    val orderSuccess: Boolean = false,
    val isOrdering: Boolean = false
)