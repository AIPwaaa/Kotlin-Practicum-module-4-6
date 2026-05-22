package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Photo

interface PhotoRepository {
    suspend fun getPhotos(page: Int = 1, limit: Int = 30): List<Photo>
}
