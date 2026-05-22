package com.example.lokamart.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokamart.data.model.Profile
import com.example.lokamart.data.repository.AuthRepository
import com.example.lokamart.data.remote.OtpApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isEmailVerified: Boolean = false,
    val pendingEmail: String? = null,
    val profile: Profile? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()
    private val otpApiService = OtpApiService()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        val user = repository.getCurrentUser()
        if (user != null) {
            _uiState.update { it.copy(isLoggedIn = true) }
            loadProfile(user.id)
        }
    }

    fun login(email: String, password: String) {

        if (email.isBlank() || password.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Email dan password tidak boleh kosong")
            }
            return
        }

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
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message
                        )
                    }
                }
        }
    }

    fun register(
        name: String,
        email: String,
        password: String
    ) {

        if (name.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Nama tidak boleh kosong")
            }
            return
        }

        if (email.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Email tidak boleh kosong")
            }
            return
        }

        if (password.length < 6) {
            _uiState.update {
                it.copy(errorMessage = "Password minimal 6 karakter")
            }
            return
        }

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            repository.register(
                name,
                email,
                password
            ).onSuccess {

                otpApiService.sendOtp(email)
                    .onSuccess {

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                pendingEmail = email,
                                successMessage = "Kode OTP telah dikirim"
                            )
                        }
                    }
                    .onFailure { e ->

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = e.message
                            )
                        }
                    }

            }.onFailure { e ->

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    fun verifyOtp(otp: String) {

        val email = uiState.value.pendingEmail

        if (email == null) {

            _uiState.update {
                it.copy(
                    errorMessage = "Email tidak ditemukan"
                )
            }

            return
        }

        if (otp.length != 6) {

            _uiState.update {
                it.copy(
                    errorMessage = "Kode OTP tidak valid"
                )
            }

            return
        }

        viewModelScope.launch {

            _uiState.update {
                it.copy(isLoading = true)
            }

            otpApiService.verifyOtp(
                email = email,
                otp = otp
            ).onSuccess {

                _uiState.update {

                    it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        isEmailVerified = true,
                        successMessage = "Verifikasi berhasil"
                    )
                }

            }.onFailure { e ->

                _uiState.update {

                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    fun resendOtp() {

        val email = uiState.value.pendingEmail ?: return

        viewModelScope.launch {

            otpApiService.sendOtp(email)
                .onSuccess {

                    _uiState.update {
                        it.copy(
                            successMessage = "OTP berhasil dikirim ulang"
                        )
                    }
                }
                .onFailure { e ->

                    _uiState.update {
                        it.copy(
                            errorMessage = e.message
                        )
                    }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.value = AuthUiState()
        }
    }

    private fun loadProfile(userId: String) {
        viewModelScope.launch {
            repository.getProfile(userId)
                .onSuccess { profile ->
                    _uiState.update { it.copy(profile = profile) }
                }
        }
    }

    fun clearMessages() {
        _uiState.update {
            it.copy(errorMessage = null, successMessage = null)
        }
    }
}