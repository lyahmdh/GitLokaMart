package com.example.lokamart.ui.viewmodel

import com.example.lokamart.data.model.Product

data class HomeUiState(

    val isLoading: Boolean = false,

    val products: List<Product> = emptyList(),

    val filteredProducts: List<Product> = emptyList(),

    val searchQuery: String = "",

    val selectedCategory: String = "Semua",

    val selectedSort: String = "Produk Terbaru",

    val errorMessage: String? = null
)