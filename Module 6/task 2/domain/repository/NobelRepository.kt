package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.NobelPrize

interface NobelRepository {
    suspend fun getNobelPrizes(year: Int?, category: String?): Result<List<NobelPrize>>
}
