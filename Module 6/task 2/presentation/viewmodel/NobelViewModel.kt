package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.NobelPrize
import com.example.myapplication.domain.usecase.GetNobelPrizesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class NobelState {
    object Loading : NobelState()
    data class Success(val prizes: List<NobelPrize>) : NobelState()
    data class Error(val message: String) : NobelState()
}

class NobelViewModel(private val getNobelPrizesUseCase: GetNobelPrizesUseCase) : ViewModel() {
    private val _state = MutableStateFlow<NobelState>(NobelState.Loading)
    val state: StateFlow<NobelState> = _state

    private var currentYear: Int? = null
    private var currentCategory: String? = null

    init {
        loadPrizes()
    }

    fun loadPrizes(year: Int? = currentYear, category: String? = currentCategory) {
        currentYear = year
        currentCategory = if (category == "all") null else category
        
        viewModelScope.launch {
            _state.value = NobelState.Loading
            getNobelPrizesUseCase(currentYear, currentCategory)
                .onSuccess { prizes ->
                    _state.value = NobelState.Success(prizes)
                }
                .onFailure { error ->
                    _state.value = NobelState.Error(error.message ?: "Unknown error")
                }
        }
    }
}
