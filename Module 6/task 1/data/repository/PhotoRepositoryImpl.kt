package com.example.myapplication.data.repository

import com.example.myapplication.data.remote.PicsumApi
import com.example.myapplication.data.remote.dto.toDomain
import com.example.myapplication.domain.model.Photo
import com.example.myapplication.domain.repository.PhotoRepository

class PhotoRepositoryImpl(private val api: PicsumApi) : PhotoRepository {
    override suspend fun getPhotos(page: Int, limit: Int): List<Photo> {
        return api.getPhotos(page, limit).map { it.toDomain() }
    }
}
