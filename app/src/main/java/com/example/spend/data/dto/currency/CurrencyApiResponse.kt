package com.example.spend.data.dto.currency

import kotlinx.serialization.Serializable

@Serializable
data class CurrencyApiResponse(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
