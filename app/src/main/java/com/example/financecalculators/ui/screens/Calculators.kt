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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financecalculators.R
import com.example.financecalculators.data.api.CurrencyApi
import com.example.financecalculators.ui.theme.*
import com.example.financecalculators.viewmodel.CompoundInterestViewModel
import com.example.financecalculators.viewmodel.CurrencyViewModel
import com.example.financecalculators.viewmodel.LoanViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompoundInterestCalculator(viewModel: CompoundInterestViewModel = viewModel()) {
    val principal by viewModel.principal.collectAsState()
    val rate by viewModel.rate.collectAsState()
    val years by viewModel.years.collectAsState()
    val frequency by viewModel.frequency.collectAsState()
    val error by viewModel.error.collectAsState()
    
    val result = viewModel.calculateResult()
    val interest = viewModel.getInterest()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (error != null) {
            Snackbar(
                containerColor = Color.Red,
                contentColor = Color.White,
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("OK", color = Color.White)
                    }
                }
            ) {
                Text(error!!)
            }
        }
        
        CalculatorInput(
            label = stringResource(R.string.ci_label_principal),
            value = principal,
            onValueChange = { viewModel.updatePrincipal(it) },
            suffix = stringResource(R.string.ci_suffix_rubles)
        )
        
        CalculatorInput(
            label = stringResource(R.string.ci_label_rate),
            value = rate,
            onValueChange = { viewModel.updateRate(it) },
            suffix = stringResource(R.string.ci_suffix_percent)
        )
        
        CalculatorInput(
            label = stringResource(R.string.ci_label_years),
            value = years,
            onValueChange = { viewModel.updateYears(it) },
            suffix = stringResource(R.string.ci_suffix_years)
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
                Text(stringResource(R.string.ci_label_frequency), color = OnDarkSurface.copy(alpha = 0.7f))
                
                listOf(
                    1 to stringResource(R.string.ci_freq_annually),
                    4 to stringResource(R.string.ci_freq_quarterly),
                    12 to stringResource(R.string.ci_freq_monthly),
                    365 to stringResource(R.string.ci_freq_daily)
                ).forEach { (freq, label) ->
                    FilterChip(
                        selected = frequency == freq,
                        onClick = { viewModel.updateFrequency(freq) },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            labelColor = if (frequency == freq) Color.White else OnDarkSurface
                        )
                    )
                }
            }
        }
        
        ResultCard(
            title = stringResource(R.string.ci_result_title),
            value = formatCurrency(result),
            subtitle = stringResource(R.string.ci_result_subtitle, formatCurrency(interest)),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanCalculator(viewModel: LoanViewModel = viewModel()) {
    val amount by viewModel.amount.collectAsState()
    val rate by viewModel.rate.collectAsState()
    val years by viewModel.years.collectAsState()
    val error by viewModel.error.collectAsState()
    
    val result = viewModel.calculateResult()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (error != null) {
            Snackbar(
                containerColor = Color.Red,
                contentColor = Color.White,
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("OK", color = Color.White)
                    }
                }
            ) {
                Text(error!!)
            }
        }
        
        CalculatorInput(
            label = stringResource(R.string.loan_label_amount),
            value = amount,
            onValueChange = { viewModel.updateAmount(it) },
            suffix = stringResource(R.string.ci_suffix_rubles)
        )
        
        CalculatorInput(
            label = stringResource(R.string.loan_label_rate),
            value = rate,
            onValueChange = { viewModel.updateRate(it) },
            suffix = stringResource(R.string.ci_suffix_percent)
        )
        
        CalculatorInput(
            label = stringResource(R.string.loan_label_years),
            value = years,
            onValueChange = { viewModel.updateYears(it) },
            suffix = stringResource(R.string.ci_suffix_years)
        )
        
        ResultCard(
            title = stringResource(R.string.loan_result_title),
            value = formatCurrency(result.monthlyPayment),
            subtitle = stringResource(
                R.string.loan_result_subtitle,
                formatCurrency(result.totalPayment),
                formatCurrency(result.overpayment)
            ),
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
fun CurrencyConverter(viewModel: CurrencyViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val fromCurrency by viewModel.fromCurrency.collectAsState()
    val toCurrency by viewModel.toCurrency.collectAsState()
    val error by viewModel.error.collectAsState()
    
    val currencies = viewModel.getCurrenciesList()
    val convertedAmount = viewModel.convertAmount()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (error != null) {
            Snackbar(
                containerColor = Color.Red,
                contentColor = Color.White,
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("OK", color = Color.White)
                    }
                }
            ) {
                Text(error!!)
            }
        }
        
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
                    text = stringResource(R.string.cc_rates_updated),
                    color = OnDarkSurface.copy(alpha = 0.7f)
                )
                IconButton(
                    onClick = {
                        viewModel.loadRates()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.cc_refresh),
                        tint = if (uiState is com.example.financecalculators.viewmodel.CurrencyUiState.Loading) Color.Gray else Primary
                    )
                }
            }
        }
        
        CalculatorInput(
            label = stringResource(R.string.cc_label_amount),
            value = amount,
            onValueChange = { viewModel.updateAmount(it) },
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
                                .menuAnchor(type = ExposedDropdownMenuBoxType.Primary),
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
                                        viewModel.updateFromCurrency(currency)
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
                                .menuAnchor(type = ExposedDropdownMenuBoxType.Secondary),
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
                                        viewModel.updateToCurrency(currency)
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
            title = stringResource(R.string.cc_result_title),
            value = "${String.format(Locale.US, "%.2f", convertedAmount)} $toCurrency",
            subtitle = stringResource(
                R.string.cc_result_subtitle,
                amount,
                fromCurrency,
                String.format(Locale.US, "%.2f", convertedAmount),
                toCurrency
            ),
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
