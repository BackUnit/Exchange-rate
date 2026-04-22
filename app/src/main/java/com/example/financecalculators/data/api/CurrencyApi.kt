package com.example.financecalculators.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class CurrencyApi {
    
    private val fallbackRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.85,
        "GBP" to 0.73,
        "JPY" to 110.0,
        "RUB" to 75.0,
        "CNY" to 6.45,
        "CHF" to 0.92,
        "CAD" to 1.25,
        "AUD" to 1.35,
        "INR" to 74.5,
        "BRL" to 5.25,
        "KRW" to 1180.0,
        "TRY" to 8.5,
        "ZAR" to 14.8,
        "MXN" to 20.1
    )

    suspend fun getExchangeRates(): Map<String, Double> = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.exchangerate-api.com/v4/latest/USD")
            val response = url.readText()
            
            val ratesStart = response.indexOf("\"rates\"") + 9
            val ratesEnd = response.indexOf("}", ratesStart)
            val ratesJson = response.substring(ratesStart, ratesEnd)
            
            val rates = mutableMapOf<String, Double>()
            rates["USD"] = 1.0
            
            val pairs = ratesJson.split(",")
            for (pair in pairs) {
                val parts = pair.split(":")
                if (parts.size == 2) {
                    val code = parts[0].trim().removeSurrounding("\"")
                    val value = parts[1].trim().toDoubleOrNull()
                    if (value != null) {
                        rates[code] = value
                    }
                }
            }
            
            if (rates.isEmpty()) fallbackRates else rates
        } catch (e: Exception) {
            fallbackRates
        }
    }
}
