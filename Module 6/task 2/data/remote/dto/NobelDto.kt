package com.example.myapplication.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NobelResponseDto(
    val nobelPrizes: List<NobelPrizeDto>
)

@Serializable
data class NobelPrizeDto(
    val awardYear: String,
    val category: TranslationDto,
    val laureates: List<LaureateDto>? = null
)

@Serializable
data class LaureateDto(
    val id: String? = null,
    val fullName: TranslationDto? = null,
    val motivation: TranslationDto? = null,
    val birth: BirthDto? = null,
    val links: List<LinkDto>? = null
)

@Serializable
data class TranslationDto(
    val en: String? = null
)

@Serializable
data class BirthDto(
    val place: PlaceDto? = null
)

@Serializable
data class PlaceDto(
    val country: TranslationDto? = null
)

@Serializable
data class LinkDto(
    val rel: String? = null,
    val href: String? = null,
    val action: String? = null,
    val types: String? = null
)
