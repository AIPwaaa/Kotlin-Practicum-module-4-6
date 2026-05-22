package com.example.myapplication.data.repository

import com.example.myapplication.data.remote.dto.NobelResponseDto
import com.example.myapplication.domain.model.Laureate
import com.example.myapplication.domain.model.NobelPrize
import com.example.myapplication.domain.repository.NobelRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class NobelRepositoryImpl(private val client: HttpClient) : NobelRepository {
    override suspend fun getNobelPrizes(year: Int?, category: String?): Result<List<NobelPrize>> {
        return try {
            val response: NobelResponseDto = client.get("https://api.nobelprize.org/2.1/nobelPrizes") {
                parameter("limit", 50)
                if (year != null) parameter("nobelPrizeYear", year)
                if (category != null) parameter("nobelPrizeCategory", category)
            }.body()

            val prizes = response.nobelPrizes.map { prizeDto ->
                NobelPrize(
                    year = prizeDto.awardYear,
                    category = prizeDto.category.en ?: "Unknown",
                    laureates = prizeDto.laureates?.map { laureateDto ->
                        Laureate(
                            id = laureateDto.id,
                            fullName = laureateDto.fullName?.en ?: "Unknown",
                            motivation = laureateDto.motivation?.en ?: "",
                            birthCountry = laureateDto.birth?.place?.country?.en,
                            portraitUrl = laureateDto.links?.find { it.rel == "external" && it.action == "GET" }?.href // Simplifying portrait detection
                        )
                    } ?: emptyList()
                )
            }
            Result.success(prizes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
