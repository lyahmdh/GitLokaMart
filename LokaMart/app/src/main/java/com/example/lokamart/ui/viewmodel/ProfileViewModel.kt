package com.example.lokamart.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokamart.data.model.Profile
import com.example.lokamart.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: Profile? = null,
    val errorMessage: String? = null,
    val isEditingName: Boolean = false,
    val isEditingPhone: Boolean = false,
    val isEditingLocation: Boolean = false,
    val editNameValue: String = "",
    val editPhoneValue: String = "",
    val editLocationValue: String = "",
    val isSaving: Boolean = false
)

class ProfileViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        val user = repository.getCurrentUser()
        android.util.Log.d("ProfileVM", "currentUser = $user")
        if (user == null) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getProfile(user.id).fold(
                onSuccess = { profile ->
                    _uiState.update { it.copy(isLoading = false, profile = profile) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun startEditName() {
        _uiState.update { it.copy(isEditingName = true, editNameValue = it.profile?.name ?: "") }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(editNameValue = value) }

    fun saveName() {
        val user = repository.getCurrentUser() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            repository.updateProfileName(user.id, _uiState.value.editNameValue).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            isEditingName = false,
                            profile = it.profile?.copy(name = it.editNameValue)
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun cancelEditName() = _uiState.update { it.copy(isEditingName = false) }

    fun startEditPhone() {
        _uiState.update { it.copy(isEditingPhone = true, editPhoneValue = it.profile?.phone ?: "") }
    }

    fun onPhoneChange(value: String) = _uiState.update { it.copy(editPhoneValue = value) }

    fun savePhone() {
        val user = repository.getCurrentUser() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            repository.updateProfilePhone(user.id, _uiState.value.editPhoneValue).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            isEditingPhone = false,
                            profile = it.profile?.copy(phone = it.editPhoneValue)
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun cancelEditPhone() = _uiState.update { it.copy(isEditingPhone = false) }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
    fun startEditLocation() {
        _uiState.update {
            it.copy(
                isEditingLocation = true,
                editLocationValue = it.profile?.location ?: ""
            )
        }
    }

    fun onLocationChange(value: String) =
        _uiState.update { it.copy(editLocationValue = value) }

    fun saveLocation() {
        val user = repository.getCurrentUser() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            repository.updateProfileLocation(user.id, _uiState.value.editLocationValue.trim()).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            isEditingLocation = false,
                            profile = it.profile?.copy(location = it.editLocationValue.trim())
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun cancelEditLocation() =
        _uiState.update { it.copy(isEditingLocation = false, editLocationValue = "") }
}