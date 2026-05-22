package com.example.myapplication.data.repository

import com.example.myapplication.data.local.TokenManager
import com.example.myapplication.data.remote.dto.*
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.repository.UserRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.first

class UserRepositoryImpl(
    private val client: HttpClient,
    private val tokenManager: TokenManager
) : UserRepository {

    private val baseUrl = "https://dummyjson.com"

    override suspend fun login(username: String, password: String): Result<String> {
        return try {
            val response: LoginResponse = client.post("$baseUrl/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("username" to username, "password" to password))
            }.body()
            tokenManager.saveToken(response.accessToken)
            Result.success(response.accessToken)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUsers(): Result<List<User>> {
        return try {
            val token = tokenManager.token.first()
            val response: UsersListResponse = client.get("$baseUrl/auth/users") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }.body()
            Result.success(response.users.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserDetail(id: Int): Result<User> {
        return try {
            val token = tokenManager.token.first()
            val response: UserDto = client.get("$baseUrl/auth/users/$id") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }.body()
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenManager.clearToken()
    }
}
