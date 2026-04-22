package com.example.financecalculators.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoanViewModel : ViewModel() {
    
    private val _amount = MutableStateFlow("1000000")
    val amount: StateFlow<String> = _amount.asStateFlow()
    
    private val _rate = MutableStateFlow("12")
    val rate: StateFlow<String> = _rate.asStateFlow()
    
    private val _years = MutableStateFlow("5")
    val years: StateFlow<String> = _years.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun updateAmount(value: String) {
        if (validateInput(value)) {
            _amount.value = value
            _error.value = null
        } else {
            _error.value = "Сумма не может быть отрицательной"
        }
    }
    
    fun updateRate(value: String) {
        if (validateInput(value)) {
            _rate.value = value
            _error.value = null
        } else {
            _error.value = "Ставка не может быть отрицательной"
        }
    }
    
    fun updateYears(value: String) {
        if (validateInput(value)) {
            _years.value = value
            _error.value = null
        } else {
            _error.value = "Срок не может быть отрицательным"
        }
    }
    
    data class LoanCalculationResult(
        val monthlyPayment: Double,
        val totalPayment: Double,
        val overpayment: Double
    )
    
    fun calculateResult(): LoanCalculationResult {
        val amount = _amount.value.toDoubleOrNull() ?: 0.0
        val annualRate = _rate.value.toDoubleOrNull() ?: 0.0
        val years = _years.value.toIntOrNull() ?: 0
        
        if (amount < 0 || annualRate < 0 || years < 0) {
            _error.value = "Сумма, ставка и срок не могут быть отрицательными"
            return LoanCalculationResult(0.0, 0.0, 0.0)
        }
        
        val result = if (annualRate == 0.0) {
            val monthlyPayment = amount / (years * 12)
            LoanCalculationResult(monthlyPayment, amount, 0.0)
        } else {
            val monthlyRate = annualRate / 100 / 12
            val months = years * 12
            val monthlyPayment = amount * monthlyRate * Math.pow(1 + monthlyRate, months) / (Math.pow(1 + monthlyRate, months) - 1)
            val totalPayment = monthlyPayment * months
            val overpayment = totalPayment - amount
            LoanCalculationResult(monthlyPayment, totalPayment, overpayment)
        }
        
        return result
    }
    
    private fun validateInput(value: String): Boolean {
        val number = value.toDoubleOrNull() ?: return true
        return number >= 0
    }
    
    fun clearError() {
        _error.value = null
    }
}
