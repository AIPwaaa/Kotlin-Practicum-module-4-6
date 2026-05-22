package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UIState<out T> {
    object Idle : UIState<Nothing>()
    object Loading : UIState<Nothing>()
    data class Success<T>(val data: T) : UIState<T>()
    data class Error(val message: String) : UIState<Nothing>()
}

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<UIState<String>>(UIState.Idle)
    val loginState: StateFlow<UIState<String>> = _loginState

    private val _usersState = MutableStateFlow<UIState<List<User>>>(UIState.Idle)
    val usersState: StateFlow<UIState<List<User>>> = _usersState

    private val _userDetailState = MutableStateFlow<UIState<User>>(UIState.Idle)
    val userDetailState: StateFlow<UIState<User>> = _userDetailState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = UIState.Loading
            loginUseCase(username, password)
                .onSuccess { _loginState.value = UIState.Success(it) }
                .onFailure { _loginState.value = UIState.Error(it.message ?: "Login failed") }
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            _usersState.value = UIState.Loading
            getUsersUseCase()
                .onSuccess { _usersState.value = UIState.Success(it) }
                .onFailure { _usersState.value = UIState.Error(it.message ?: "Failed to load users") }
        }
    }

    fun loadUserDetail(id: Int) {
        viewModelScope.launch {
            _userDetailState.value = UIState.Loading
            getUserDetailUseCase(id)
                .onSuccess { _userDetailState.value = UIState.Success(it) }
                .onFailure { _userDetailState.value = UIState.Error(it.message ?: "Failed to load user details") }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _loginState.value = UIState.Idle
        }
    }
}
