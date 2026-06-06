package com.example.lokamart.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokamart.data.repository.AuthRepository
import com.example.lokamart.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderHistoryViewModel : ViewModel() {

    private val orderRepository = OrderRepository()
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(OrderHistoryUiState())
    val uiState: StateFlow<OrderHistoryUiState> = _uiState.asStateFlow()

    init { loadOrders() }

    fun loadOrders() {
        val userId = authRepository.getCurrentUser()?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            orderRepository.getMyOrdersWithProduct(userId)
                .onSuccess { orders ->
                    android.util.Log.d("OrderHistoryVM", "Orders loaded: ${orders.size}")
                    orders.forEach { android.util.Log.d("OrderHistoryVM", "Order: $it") }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            orders = orders,
                            filteredOrders = orders
                        )
                    }
                }
                .onFailure { e ->
                    android.util.Log.e("OrderHistoryVM", "Error: ${e.message}", e)
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message)
                    }
                }
        }
    }

    fun filterByCategory(category: String) {
        _uiState.update { state ->
            val filtered = if (category == "Semua") state.orders
            else state.orders.filter {
                it.products?.category?.trim()?.lowercase() == category.trim().lowercase()
            }
            state.copy(selectedCategory = category, filteredOrders = filtered)
        }
    }
}