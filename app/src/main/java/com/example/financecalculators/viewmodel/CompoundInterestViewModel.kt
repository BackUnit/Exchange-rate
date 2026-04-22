package com.example.financecalculators.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class CalculatorUiState {
    object Initial : CalculatorUiState()
    object Loading : CalculatorUiState()
    data class Success(val result: Double) : CalculatorUiState()
    data class Error(val message: String) : CalculatorUiState()
}

class CompoundInterestViewModel : ViewModel() {
    
    private val _principal = MutableStateFlow("100000")
    val principal: StateFlow<String> = _principal.asStateFlow()
    
    private val _rate = MutableStateFlow("10")
    val rate: StateFlow<String> = _rate.asStateFlow()
    
    private val _years = MutableStateFlow("5")
    val years: StateFlow<String> = _years.asStateFlow()
    
    private val _frequency = MutableStateFlow(12)
    val frequency: StateFlow<Int> = _frequency.asStateFlow()
    
    private val _uiState = MutableStateFlow<CalculatorUiState>(CalculatorUiState.Initial)
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun updatePrincipal(value: String) {
        if (validateInput(value)) {
            _principal.value = value
            _error.value = null
        } else {
            _error.value = "Значение не может быть отрицательным"
        }
    }
    
    fun updateRate(value: String) {
        if (validateInput(value)) {
            _rate.value = value
            _error.value = null
        } else {
            _error.value = "Значение не может быть отрицательным"
        }
    }
    
    fun updateYears(value: String) {
        if (validateInput(value)) {
            _years.value = value
            _error.value = null
        } else {
            _error.value = "Значение не может быть отрицательным"
        }
    }
    
    fun updateFrequency(freq: Int) {
        _frequency.value = freq
    }
    
    fun calculateResult(): Double {
        val p = _principal.value.toDoubleOrNull() ?: 0.0
        val r = _rate.value.toDoubleOrNull() ?: 0.0
        val y = _years.value.toIntOrNull() ?: 0
        val f = _frequency.value
        
        if (p < 0 || r < 0 || y < 0) {
            _error.value = "Значения не могут быть отрицательными"
            return 0.0
        }
        
        val ratePerPeriod = r / 100 / f
        val totalPeriods = y * f
        val finalAmount = p * Math.pow(1 + ratePerPeriod, totalPeriods)
        
        _uiState.value = CalculatorUiState.Success(finalAmount)
        return finalAmount
    }
    
    fun getInterest(): Double {
        val finalAmount = calculateResult()
        val principal = _principal.value.toDoubleOrNull() ?: 0.0
        return finalAmount - principal
    }
    
    private fun validateInput(value: String): Boolean {
        val number = value.toDoubleOrNull() ?: return true
        return number >= 0
    }
    
    fun clearError() {
        _error.value = null
    }
}
