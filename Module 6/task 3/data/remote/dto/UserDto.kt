package com.example.myapplication.data.remote.dto

import com.example.myapplication.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val image: String,
    val accessToken: String
)

@Serializable
data class UsersListResponse(
    val users: List<UserDto>
)

@Serializable
data class UserDto(
    val id: Int,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val image: String
)

fun UserDto.toDomain() = User(
    id = id,
    username = username,
    firstName = firstName,
    lastName = lastName,
    email = email,
    image = image
)
