package com.example.lokamart.ui.viewmodel

import com.example.lokamart.data.model.OrderWithProduct

data class OrderHistoryUiState(
    val isLoading: Boolean = false,
    val orders: List<OrderWithProduct> = emptyList(),
    val filteredOrders: List<OrderWithProduct> = emptyList(),
    val selectedCategory: String = "Semua",
    val errorMessage: String? = null
)