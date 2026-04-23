package com.example.financecalculators.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financecalculators.data.api.CurrencyApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CurrencyUiState {
    object Initial : CurrencyUiState()
    object Loading : CurrencyUiState()
    data class Success(val rates: Map<String, Double>) : CurrencyUiState()
    data class Error(val message: String) : CurrencyUiState()
}

class CurrencyViewModel : ViewModel() {
    
    private val currencyApi = CurrencyApi()
    
    private val _uiState = MutableStateFlow<CurrencyUiState>(CurrencyUiState.Initial)
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()
    
    private val _amount = MutableStateFlow("1000")
    val amount: StateFlow<String> = _amount.asStateFlow()
    
    private val _fromCurrency = MutableStateFlow("USD")
    val fromCurrency: StateFlow<String> = _fromCurrency.asStateFlow()
    
    private val _toCurrency = MutableStateFlow("EUR")
    val toCurrency: StateFlow<String> = _toCurrency.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadRates()
    }
    
    fun loadRates() {
        _uiState.value = CurrencyUiState.Loading
        viewModelScope.launch {
            try {
                val rates = currencyApi.getExchangeRates()
                _uiState.value = CurrencyUiState.Success(rates)
                _error.value = null
            } catch (e: Exception) {
                _uiState.value = CurrencyUiState.Error("Ошибка загрузки курсов валют")
                _error.value = "Ошибка загрузки курсов валют"
            }
        }
    }
    
    fun updateAmount(value: String) {
        if (validateInput(value)) {
            _amount.value = value
            _error.value = null
        } else {
            _error.value = "Сумма не может быть отрицательной"
        }
    }
    
    fun updateFromCurrency(currency: String) {
        _fromCurrency.value = currency
    }
    
    fun updateToCurrency(currency: String) {
        _toCurrency.value = currency
    }
    
    fun convertAmount(): Double {
        val amount = _amount.value.toDoubleOrNull() ?: 0.0
        
        if (amount < 0) {
            _error.value = "Сумма не может быть отрицательной"
            return 0.0
        }
        
        val currentState = _uiState.value
        if (currentState !is CurrencyUiState.Success) {
            return 0.0
        }
        
        val rates = currentState.rates
        val fromRate = rates[_fromCurrency.value] ?: 1.0
        val toRate = rates[_toCurrency.value] ?: 1.0
        
        return (amount / fromRate) * toRate
    }
    
    private fun validateInput(value: String): Boolean {
        val number = value.toDoubleOrNull() ?: return true
        return number >= 0
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun getCurrenciesList(): List<String> {
        val currentState = _uiState.value
        return if (currentState is CurrencyUiState.Success) {
            currentState.rates.keys.toList().sorted()
        } else {
            emptyList()
        }
    }
}
