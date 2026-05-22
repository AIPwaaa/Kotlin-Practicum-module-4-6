package com.example.myapplication.data.remote.dto

import com.example.myapplication.domain.model.Photo
import com.google.gson.annotations.SerializedName

data class PhotoDto(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,
    @SerializedName("download_url")
    val downloadUrl: String
)

fun PhotoDto.toDomain(): Photo {
    return Photo(
        id = id,
        author = author,
        width = width,
        height = height,
        url = url,
        downloadUrl = downloadUrl
    )
}
