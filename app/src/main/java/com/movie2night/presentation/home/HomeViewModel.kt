package com.movie2night.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movie2night.domain.model.Movie
import com.movie2night.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            movieRepository.getMovies()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { movies ->
                    _uiState.update { it.copy(isLoading = false, movies = movies) }
                }
        }
    }
}
