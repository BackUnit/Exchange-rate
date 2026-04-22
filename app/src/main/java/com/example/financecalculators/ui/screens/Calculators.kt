package com.example.financecalculators.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financecalculators.data.api.CurrencyApi
import com.example.financecalculators.ui.theme.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CompoundInterestCalculator() {
    var principal by remember { mutableStateOf("100000") }
    var rate by remember { mutableStateOf("10") }
    var years by remember { mutableStateOf("5") }
    var compoundFrequency by remember { mutableStateOf(12) }
    
    val result = calculateCompoundInterest(
        principal.toDoubleOrNull() ?: 0.0,
        rate.toDoubleOrNull() ?: 0.0,
        years.toIntOrNull() ?: 0,
        compoundFrequency
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CalculatorInput(
            label = "Начальная сумма",
            value = principal,
            onValueChange = { principal = it },
            suffix = "₽"
        )
        
        CalculatorInput(
            label = "Процентная ставка (годовых)",
            value = rate,
            onValueChange = { rate = it },
            suffix = "%"
        )
        
        CalculatorInput(
            label = "Срок инвестирования",
            value = years,
            onValueChange = { years = it },
            suffix = "лет"
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Частота начисления процентов", color = OnDarkSurface.copy(alpha = 0.7f))
                
                listOf(
                    1 to "Ежегодно",
                    4 to "Ежеквартально",
                    12 to "Ежемесячно",
                    365 to "Ежедневно"
                ).forEach { (freq, label) ->
                    FilterChip(
                        selected = compoundFrequency == freq,
                        onClick = { compoundFrequency = freq },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            labelColor = if (compoundFrequency == freq) Color.White else OnDarkSurface
                        )
                    )
                }
            }
        }
        
        ResultCard(
            title = "Итоговая сумма",
            value = formatCurrency(result.finalAmount),
            subtitle = "Доход: ${formatCurrency(result.interest)}",
            gradientColors = listOf(AccentGreen, AccentBlue)
        )
    }
}

data class CompoundInterestResult(
    val finalAmount: Double,
    val interest: Double
)

fun calculateCompoundInterest(
    principal: Double,
    rate: Double,
    years: Int,
    frequency: Int
): CompoundInterestResult {
    val r = rate / 100 / frequency
    val n = years * frequency
    val finalAmount = principal * Math.pow(1 + r, n)
    return CompoundInterestResult(finalAmount, finalAmount - principal)
}

@Composable
fun LoanCalculator() {
    var amount by remember { mutableStateOf("1000000") }
    var rate by remember { mutableStateOf("12") }
    var years by remember { mutableStateOf("5") }
    
    val result = calculateLoan(
        amount.toDoubleOrNull() ?: 0.0,
        rate.toDoubleOrNull() ?: 0.0,
        years.toIntOrNull() ?: 0
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CalculatorInput(
            label = "Сумма кредита",
            value = amount,
            onValueChange = { amount = it },
            suffix = "₽"
        )
        
        CalculatorInput(
            label = "Процентная ставка (годовых)",
            value = rate,
            onValueChange = { rate = it },
            suffix = "%"
        )
        
        CalculatorInput(
            label = "Срок кредита",
            value = years,
            onValueChange = { years = it },
            suffix = "лет"
        )
        
        ResultCard(
            title = "Ежемесячный платеж",
            value = formatCurrency(result.monthlyPayment),
            subtitle = "Общая выплата: ${formatCurrency(result.totalPayment)}\nПереплата: ${formatCurrency(result.overpayment)}",
            gradientColors = listOf(AccentOrange, AccentPurple)
        )
    }
}

data class LoanResult(
    val monthlyPayment: Double,
    val totalPayment: Double,
    val overpayment: Double
)

fun calculateLoan(
    amount: Double,
    annualRate: Double,
    years: Int
): LoanResult {
    if (annualRate == 0.0) {
        val monthlyPayment = amount / (years * 12)
        return LoanResult(monthlyPayment, amount, 0.0)
    }
    
    val monthlyRate = annualRate / 100 / 12
    val months = years * 12
    val monthlyPayment = amount * monthlyRate * Math.pow(1 + monthlyRate, months) / (Math.pow(1 + monthlyRate, months) - 1)
    val totalPayment = monthlyPayment * months
    val overpayment = totalPayment - amount
    
    return LoanResult(monthlyPayment, totalPayment, overpayment)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverter() {
    val currencyApi = remember { CurrencyApi() }
    var exchangeRates by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var amount by remember { mutableStateOf("1000") }
    var fromCurrency by remember { mutableStateOf("USD") }
    var toCurrency by remember { mutableStateOf("EUR") }
    
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        exchangeRates = currencyApi.getExchangeRates()
        isLoading = false
    }
    
    val currencies = remember(exchangeRates) {
        exchangeRates.keys.toList().sorted()
    }
    
    val convertedAmount = remember(amount, fromCurrency, toCurrency, exchangeRates) {
        if (exchangeRates.isEmpty()) return@remember 0.0
        val usdAmount = amount.toDoubleOrNull() ?: 0.0
        val fromRate = exchangeRates[fromCurrency] ?: 1.0
        val toRate = exchangeRates[toCurrency] ?: 1.0
        (usdAmount / fromRate) * toRate
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Курсы валют обновлены",
                    color = OnDarkSurface.copy(alpha = 0.7f)
                )
                IconButton(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            exchangeRates = currencyApi.getExchangeRates()
                            isLoading = false
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Обновить",
                        tint = if (isLoading) Color.Gray else Primary
                    )
                }
            }
        }
        
        CalculatorInput(
            label = "Сумма",
            value = amount,
            onValueChange = { amount = it },
            suffix = ""
        )
        
        if (currencies.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var expandedFrom by remember { mutableStateOf(false) }
                var expandedTo by remember { mutableStateOf(false) }
                
                Box(modifier = Modifier.weight(1f)) {
                    ExposedDropdownMenuBox(
                        expanded = expandedFrom,
                        onExpandedChange = { expandedFrom = it }
                    ) {
                        OutlinedTextField(
                            value = fromCurrency,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = DarkSurfaceVariant
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedFrom,
                            onDismissRequest = { expandedFrom = false }
                        ) {
                            currencies.forEach { currency ->
                                DropdownMenuItem(
                                    text = { Text(currency) },
                                    onClick = {
                                        fromCurrency = currency
                                        expandedFrom = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                Box(modifier = Modifier.weight(1f)) {
                    ExposedDropdownMenuBox(
                        expanded = expandedTo,
                        onExpandedChange = { expandedTo = it }
                    ) {
                        OutlinedTextField(
                            value = toCurrency,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = DarkSurfaceVariant
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedTo,
                            onDismissRequest = { expandedTo = false }
                        ) {
                            currencies.forEach { currency ->
                                DropdownMenuItem(
                                    text = { Text(currency) },
                                    onClick = {
                                        toCurrency = currency
                                        expandedTo = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        
        ResultCard(
            title = "Результат конвертации",
            value = "${String.format(Locale.US, "%.2f", convertedAmount)} $toCurrency",
            subtitle = "$amount $fromCurrency = ${String.format(Locale.US, "%.2f", convertedAmount)} $toCurrency",
            gradientColors = listOf(AccentPurple, AccentBlue)
        )
    }
}

@Composable
fun CalculatorInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, color = OnDarkSurface.copy(alpha = 0.7f), fontSize = 14.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            suffix = { Text(suffix, color = Primary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = DarkSurfaceVariant,
                focusedLabelColor = Primary,
                unfocusedLabelColor = OnDarkSurface.copy(alpha = 0.5f)
            ),
            textStyle = LocalTextStyle.current.copy(color = OnDarkSurface)
        )
    }
}

@Composable
fun ResultCard(
    title: String,
    value: String,
    subtitle: String,
    gradientColors: List<Color>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = gradientColors,
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )
                
                Column {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

fun formatCurrency(amount: Double): String {
    return NumberFormat.getNumberInstance(Locale("ru", "RU")).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }.format(amount) + " ₽"
}
