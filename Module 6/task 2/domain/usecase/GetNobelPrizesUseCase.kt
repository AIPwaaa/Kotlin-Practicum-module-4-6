package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.NobelPrize
import com.example.myapplication.domain.repository.NobelRepository

class GetNobelPrizesUseCase(private val repository: NobelRepository) {
    suspend operator fun invoke(year: Int? = null, category: String? = null): Result<List<NobelPrize>> {
        return repository.getNobelPrizes(year, category)
    }
}
