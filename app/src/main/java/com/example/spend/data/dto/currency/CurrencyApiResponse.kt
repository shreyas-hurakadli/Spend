package com.example.spend.data.dto.currency

data class CurrencyApiResponse(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
