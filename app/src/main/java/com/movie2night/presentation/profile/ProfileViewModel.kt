package com.movie2night.presentation.profile

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val isSaveSuccess: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    // Campos editáveis da tela
    var name by mutableStateOf("")
    var bio by mutableStateOf("")
    var intention by mutableStateOf(UserIntention.OPEN)
    var photoUri by mutableStateOf<Uri?>(null)     // foto selecionada da galeria
    var photoUrl by mutableStateOf<String?>(null)  // URL atual salva no servidor

    fun loadUser(userId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            val user = userRepository.getUserById(userId)
            if (user != null) {
                name = user.name
                bio = user.bio ?: ""
                intention = user.intention
                photoUrl = user.photoUrl
                uiState = uiState.copy(isLoading = false, user = user)
            } else {
                uiState = uiState.copy(isLoading = false, errorMessage = "Usuário não encontrado")
            }
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, errorMessage = null)
            userRepository.updateProfile(
                name = name.trim(),
                bio = bio.trim().ifBlank { null },
                photoUrl = photoUrl
            )
                .onSuccess {
                    uiState = uiState.copy(isSaving = false, isSaveSuccess = true, user = it)
                }
                .onFailure {
                    uiState = uiState.copy(isSaving = false, errorMessage = it.message)
                }
        }
    }

    fun onPhotoSelected(uri: Uri) {
        photoUri = uri
        // Aqui você futuramente fará o upload para o servidor e salvará a URL retornada
        // Por enquanto apenas exibe localmente
    }

    fun clearSuccess() {
        uiState = uiState.copy(isSaveSuccess = false)
    }
}
