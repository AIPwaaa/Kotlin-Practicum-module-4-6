package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Photo
import com.example.myapplication.domain.repository.PhotoRepository

class GetPhotosUseCase(private val repository: PhotoRepository) {
    suspend operator fun invoke(): Result<List<Photo>> {
        return try {
            Result.success(repository.getPhotos())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
