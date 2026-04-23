package com.example.financecalculators.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel for Compound Interest Calculator
 * Implements MVVM pattern with StateFlow for reactive UI updates
 */
class CalculationViewModel : ViewModel() {

    // Input state
    private val _uiState = MutableStateFlow(CompoundInterestUiState())
    val uiState: StateFlow<CompoundInterestUiState> = _uiState.asStateFlow()

    // Update initial capital
    fun updateInitialCapital(capital: String) {
        _uiState.update { it.copy(initialCapital = capital) }
    }

    // Update monthly replenishment
    fun updateMonthlyReplenishment(amount: String) {
        _uiState.update { it.copy(monthlyReplenishment = amount) }
    }

    // Update interest rate
    fun updateInterestRate(rate: String) {
        _uiState.update { it.copy(interestRate = rate) }
    }

    // Update term years
    fun updateTermYears(years: String) {
        _uiState.update { it.copy(termYears = years) }
    }

    // Update term months
    fun updateTermMonths(months: String) {
        _uiState.update { it.copy(termMonths = months) }
    }

    // Update compounding frequency
    fun updateCompoundingFrequency(frequency: CompoundingFrequency) {
        _uiState.update { it.copy(compoundingFrequency = frequency) }
    }

    // Reset to default values
    fun resetToDefaults() {
        _uiState.value = CompoundInterestUiState()
    }
}

/**
 * Compounding frequency options
 */
enum class CompoundingFrequency(val value: Int, val labelRes: String) {
    YEARLY(1, "Ежегодно"),
    QUARTERLY(4, "Ежеквартально"),
    MONTHLY(12, "Ежемесячно"),
    DAILY(365, "Ежедневно")
}

/**
 * UI State data class for Compound Interest Calculator
 */
data class CompoundInterestUiState(
    val initialCapital: String = "100000",
    val monthlyReplenishment: String = "0",
    val interestRate: String = "10",
    val termYears: String = "5",
    val termMonths: String = "0",
    val compoundingFrequency: CompoundingFrequency = CompoundingFrequency.MONTHLY
) {
    // Calculate total term in years (including fractional years from months)
    val totalTermInYears: Double
        get() {
            val years = termYears.toDoubleOrNull() ?: 0.0
            val months = termMonths.toDoubleOrNull() ?: 0.0
            return years + (months / 12.0)
        }

    // Calculate total term in months
    val totalTermInMonths: Int
        get() {
            val years = termYears.toIntOrNull() ?: 0
            val months = termMonths.toIntOrNull() ?: 0
            return years * 12 + months
        }
}

/**
 * Calculation result data class
 */
data class CompoundInterestResult(
    val finalAmount: Double,
    val totalInvested: Double,
    val totalInterest: Double,
    val yearlyBreakdown: List<YearlyBreakdown>
)

/**
 * Yearly breakdown for detailed table
 */
data class YearlyBreakdown(
    val year: Int,
    val startBalance: Double,
    val deposits: Double,
    val interestEarned: Double,
    val endBalance: Double
)

/**
 * Core calculation logic matching profinansy.ru formula
 * 
 * Formula for compound interest with regular contributions:
 * FV = P * (1 + r/n)^(n*t) + PMT * [((1 + r/n)^(n*t) - 1) / (r/n)]
 * 
 * Where:
 * - P = initial principal
 * - PMT = monthly payment (replenishment)
 * - r = annual interest rate (decimal)
 * - n = compounding frequency per year
 * - t = time in years
 */
fun calculateCompoundInterest(state: CompoundInterestUiState): CompoundInterestResult {
    val principal = state.initialCapital.toDoubleOrNull() ?: 0.0
    val monthlyPayment = state.monthlyReplenishment.toDoubleOrNull() ?: 0.0
    val annualRate = state.interestRate.toDoubleOrNull() ?: 0.0
    val totalMonths = state.totalTermInMonths
    val frequency = state.compoundingFrequency.value
    
    if (annualRate == 0.0 || totalMonths == 0) {
        val totalInvested = principal + (monthlyPayment * totalMonths)
        return CompoundInterestResult(
            finalAmount = totalInvested,
            totalInvested = totalInvested,
            totalInterest = 0.0,
            yearlyBreakdown = calculateYearlyBreakdownZeroInterest(principal, monthlyPayment, totalMonths)
        )
    }

    val ratePerPeriod = annualRate / 100.0 / frequency
    val totalPeriods = frequency * state.totalTermInYears
    
    // Calculate future value of initial principal
    val fvPrincipal = principal * Math.pow(1 + ratePerPeriod, totalPeriods)
    
    // Calculate future value of monthly contributions
    // Since contributions are monthly but compounding may be different, we need to adjust
    val fvPayments = if (ratePerPeriod > 0) {
        monthlyPayment * (Math.pow(1 + ratePerPeriod, totalPeriods) - 1) / ratePerPeriod * (ratePerPeriod / 12.0)
    } else {
        monthlyPayment * totalMonths
    }
    
    val finalAmount = fvPrincipal + fvPayments
    val totalInvested = principal + (monthlyPayment * totalMonths)
    val totalInterest = finalAmount - totalInvested
    
    val yearlyBreakdown = calculateYearlyBreakdown(
        principal = principal,
        monthlyPayment = monthlyPayment,
        annualRate = annualRate,
        frequency = frequency,
        totalMonths = totalMonths
    )
    
    return CompoundInterestResult(
        finalAmount = finalAmount,
        totalInvested = totalInvested,
        totalInterest = totalInterest,
        yearlyBreakdown = yearlyBreakdown
    )
}

private fun calculateYearlyBreakdownZeroInterest(
    principal: Double,
    monthlyPayment: Double,
    totalMonths: Int
): List<YearlyBreakdown> {
    val breakdown = mutableListOf<YearlyBreakdown>()
    var balance = principal
    val years = (totalMonths + 11) / 12 // Round up for display
    
    for (year in 1..years) {
        val monthsInYear = if (year == years) totalMonths % 12 else 12
        val depositsThisYear = monthlyPayment * monthsInYear
        val startBalance = balance
        balance += depositsThisYear
        
        breakdown.add(
            YearlyBreakdown(
                year = year,
                startBalance = startBalance,
                deposits = depositsThisYear,
                interestEarned = 0.0,
                endBalance = balance
            )
        )
    }
    
    return breakdown
}

private fun calculateYearlyBreakdown(
    principal: Double,
    monthlyPayment: Double,
    annualRate: Double,
    frequency: Int,
    totalMonths: Int
): List<YearlyBreakdown> {
    val breakdown = mutableListOf<YearlyBreakdown>()
    var balance = principal
    val ratePerPeriod = annualRate / 100.0 / frequency
    val periodsPerMonth = frequency / 12.0
    
    val years = (totalMonths + 11) / 12
    
    for (year in 1..years) {
        val monthsInYear = minOf(12, totalMonths - (year - 1) * 12)
        if (monthsInYear <= 0) break
        
        val startBalance = balance
        var interestThisYear = 0.0
        var depositsThisYear = 0.0
        
        for (month in 1..monthsInYear) {
            // Add monthly contribution at the beginning of the month
            balance += monthlyPayment
            depositsThisYear += monthlyPayment
            
            // Apply compound interest for this month's periods
            val periodsInMonth = periodsPerMonth
            for (p in 1..periodsInMonth.toInt()) {
                val interest = balance * ratePerPeriod
                balance += interest
                interestThisYear += interest
            }
            
            // Handle fractional periods (e.g., daily compounding)
            val fractionalPart = periodsInMonth - periodsInMonth.toInt()
            if (fractionalPart > 0) {
                val interest = balance * ratePerPeriod * fractionalPart
                balance += interest
                interestThisYear += interest
            }
        }
        
        breakdown.add(
            YearlyBreakdown(
                year = year,
                startBalance = startBalance,
                deposits = depositsThisYear,
                interestEarned = interestThisYear,
                endBalance = balance
            )
        )
    }
    
    return breakdown
}
