package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.repository.UserRepository

class LoginUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(username: String, password: String) = repository.login(username, password)
}

class GetUsersUseCase(private val repository: UserRepository) {
    suspend operator fun invoke() = repository.getUsers()
}

class GetUserDetailUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(id: Int) = repository.getUserDetail(id)
}

class LogoutUseCase(private val repository: UserRepository) {
    suspend operator fun invoke() = repository.logout()
}
