package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.User

interface UserRepository {
    suspend fun login(username: String, password: String): Result<String>
    suspend fun getUsers(): Result<List<User>>
    suspend fun getUserDetail(id: Int): Result<User>
    suspend fun logout()
}
