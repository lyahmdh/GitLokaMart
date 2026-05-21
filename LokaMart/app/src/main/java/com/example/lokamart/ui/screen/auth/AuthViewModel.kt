package com.example.lokamart.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokamart.data.model.Profile
import com.example.lokamart.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ── UI State ──────────────────────────────────────────────────
data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val profile: Profile? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkSession()
    }

    // ── Cek sesi saat app dibuka ──────────────────────────────
    private fun checkSession() {
        val user = repository.getCurrentUser()
        if (user != null) {
            _uiState.update { it.copy(isLoggedIn = true) }
            loadProfile(user.id)
        }
    }

    // ── Register ──────────────────────────────────────────────
    fun register(name: String, email: String, password: String) {
        if (!validateRegisterInput(name, email, password)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.register(name, email, password)
                .onSuccess { user ->
                    loadProfile(user.id)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            successMessage = "Registrasi berhasil!"
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
        }
    }

    // ── Login ─────────────────────────────────────────────────
    fun login(email: String, password: String) {
        if (!validateLoginInput(email, password)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.login(email, password)
                .onSuccess { user ->
                    loadProfile(user.id)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
        }
    }

    // ── Logout ────────────────────────────────────────────────
    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.logout()
            _uiState.update {
                AuthUiState(isLoggedIn = false)
            }
        }
    }

    // ── Load profil user ──────────────────────────────────────
    private fun loadProfile(userId: String) {
        viewModelScope.launch {
            repository.getProfile(userId)
                .onSuccess { profile ->
                    _uiState.update { it.copy(profile = profile) }
                }
        }
    }

    // ── Hapus pesan error / sukses ────────────────────────────
    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    // ── Validasi input Register ───────────────────────────────
    private fun validateRegisterInput(
        name: String,
        email: String,
        password: String
    ): Boolean {
        return when {
            name.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Nama tidak boleh kosong") }
                false
            }
            name.length < 2 -> {
                _uiState.update { it.copy(errorMessage = "Nama minimal 2 karakter") }
                false
            }
            email.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Email tidak boleh kosong") }
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _uiState.update { it.copy(errorMessage = "Format email tidak valid") }
                false
            }
            password.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Password tidak boleh kosong") }
                false
            }
            password.length < 6 -> {
                _uiState.update { it.copy(errorMessage = "Password minimal 6 karakter") }
                false
            }
            else -> true
        }
    }

    // ── Validasi input Login ──────────────────────────────────
    private fun validateLoginInput(email: String, password: String): Boolean {
        return when {
            email.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Email tidak boleh kosong") }
                false
            }
            password.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Password tidak boleh kosong") }
                false
            }
            else -> true
        }
    }
}
