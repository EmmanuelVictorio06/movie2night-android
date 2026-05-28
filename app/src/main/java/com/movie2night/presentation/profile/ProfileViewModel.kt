package com.movie2night.presentation.profile

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movie2night.data.local.datastore.AuthDataStore
import com.movie2night.domain.model.User
import com.movie2night.domain.model.UserIntention
import com.movie2night.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val user: User? = null,
    val errorMessage: String? = null,
    val isSaveSuccess: Boolean = false,
    val isReportSuccess: Boolean = false,
    val isBlockSuccess: Boolean = false,
    val isLoggedOut: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authDataStore: AuthDataStore
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    var name by mutableStateOf("")
    var bio by mutableStateOf("")
    var intention by mutableStateOf(UserIntention.OPEN)
    var photoUri by mutableStateOf<Uri?>(null)
    var photoUrl by mutableStateOf<String?>(null)
    var currentUserId by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            currentUserId = authDataStore.getUserId() ?: ""
        }
    }

    // Carrega o próprio perfil usando o userId do DataStore
    fun loadMyProfile() {
        viewModelScope.launch {
            val userId = authDataStore.getUserId() ?: return@launch
            currentUserId = userId
            loadUser(userId)
        }
    }

    fun loadUser(userId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            val user = userRepository.getUserById(userId)
            if (user != null) {
                name      = user.name
                bio       = user.bio ?: ""
                intention = user.intention
                photoUrl  = user.photoUrl
                uiState   = uiState.copy(isLoading = false, user = user)
            } else {
                uiState = uiState.copy(isLoading = false, errorMessage = "Usuário não encontrado")
            }
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, errorMessage = null)
            userRepository.updateProfile(
                name      = name.trim(),
                bio       = bio.trim().ifBlank { null },
                photoUrl  = photoUrl,
                intention = intention.name
            )
                .onSuccess { uiState = uiState.copy(isSaving = false, isSaveSuccess = true, user = it) }
                .onFailure { uiState = uiState.copy(isSaving = false, errorMessage = it.message) }
        }
    }

    fun reportUser(reportedUserId: String, reason: String) {
        viewModelScope.launch {
            userRepository.reportUser(reportedUserId, reason, null)
                .onSuccess { uiState = uiState.copy(isReportSuccess = true) }
                .onFailure { uiState = uiState.copy(errorMessage = "Erro ao enviar denúncia") }
        }
    }

    fun blockUser(userId: String) {
        viewModelScope.launch {
            userRepository.blockUser(userId)
                .onSuccess { uiState = uiState.copy(isBlockSuccess = true) }
                .onFailure { uiState = uiState.copy(errorMessage = "Erro ao bloquear usuário") }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authDataStore.clear()
            uiState = uiState.copy(isLoggedOut = true)
        }
    }

    fun onPhotoSelected(uri: Uri) {
        photoUri = uri
    }

    fun clearSuccess() {
        uiState = uiState.copy(isSaveSuccess = false, isReportSuccess = false, isBlockSuccess = false)
    }
}