package com.example.financecalculators.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import java.net.HttpURLConnection
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
            // Добавляем таймаут 5 секунд для HTTP запроса
            val response = withTimeoutOrNull(5000L) {
                val url = URL("https://api.exchangerate-api.com/v4/latest/USD")
                val connection = url.openConnection() as HttpURLConnection
                try {
                    connection.connectTimeout = 5000
                    connection.readTimeout = 5000
                    connection.requestMethod = "GET"
                    connection.inputStream.bufferedReader().use { it.readText() }
                } finally {
                    connection.disconnect()
                }
            }
            
            if (response == null) {
                return@withContext fallbackRates
            }
            
            // Безопасный парсинг JSON через JSONObject
            val jsonResponse = JSONObject(response)
            val ratesObject = jsonResponse.optJSONObject("rates") ?: return@withContext fallbackRates
            
            val rates = mutableMapOf<String, Double>()
            rates["USD"] = 1.0
            
            ratesObject.keys().forEach { key ->
                rates[key] = ratesObject.optDouble(key, 1.0)
            }
            
            if (rates.isEmpty()) fallbackRates else rates
        } catch (e: Exception) {
            fallbackRates
        }
    }
}
