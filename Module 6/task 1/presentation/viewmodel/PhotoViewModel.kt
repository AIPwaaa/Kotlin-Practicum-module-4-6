package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Photo
import com.example.myapplication.domain.usecase.GetPhotosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PhotoListState {
    object Loading : PhotoListState()
    data class Success(val photos: List<Photo>) : PhotoListState()
    data class Error(val message: String) : PhotoListState()
}

class PhotoViewModel(private val getPhotosUseCase: GetPhotosUseCase) : ViewModel() {
    private val _state = MutableStateFlow<PhotoListState>(PhotoListState.Loading)
    val state: StateFlow<PhotoListState> = _state

    init {
        loadPhotos()
    }

    fun loadPhotos() {
        viewModelScope.launch {
            _state.value = PhotoListState.Loading
            getPhotosUseCase()
                .onSuccess { photos ->
                    _state.value = PhotoListState.Success(photos)
                }
                .onFailure { error ->
                    _state.value = PhotoListState.Error(error.message ?: "Unknown error")
                }
        }
    }
}
