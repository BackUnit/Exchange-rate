package com.example.financecalculators.data.model

data class CurrencyRate(
    val code: String,
    val name: String,
    val rate: Double,
    val icon: String
)

data class ExchangeRatesResponse(
    val base: String,
    val rates: Map<String, Double>,
    val date: String
)
