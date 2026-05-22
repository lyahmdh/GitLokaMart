package com.example.lokamart.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokamart.data.model.Product
import com.example.lokamart.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    // ✅ Fix: nama class diseragamkan jadi ProductRepository
    private val repository = ProductRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
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
                it.name.contains(state.searchQuery, ignoreCase = true)
            }
        }

        // CATEGORY
        if (state.selectedCategory != "Semua") {
            result = result.filter { it.category == state.selectedCategory }
        }

        // SORT
        // ✅ Fix 1: it.createdAt (bukan it.created_at)
        // ✅ Fix 2: sort Rating pakai it.rating (bukan it.price)
        result = if (state.selectedSort == "Produk Terbaru") {
            result.sortedByDescending { it.createdAt }
        } else {
            result.sortedByDescending { it.rating }
        }

        _uiState.update { it.copy(filteredProducts = result) }
    }
}
