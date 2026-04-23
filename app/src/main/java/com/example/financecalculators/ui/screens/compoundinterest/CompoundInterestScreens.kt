package com.example.financecalculators.ui.screens.compoundinterest

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financecalculators.ui.theme.*
import com.example.financecalculators.viewmodel.CompoundingFrequency
import com.example.financecalculators.viewmodel.CompoundInterestUiState
import com.example.financecalculators.viewmodel.CalculationViewModel
import com.example.financecalculators.viewmodel.calculateCompoundInterest
import java.text.NumberFormat
import java.util.Locale

/**
 * Animated number counter for real-time result header
 */
@Composable
fun AnimatedNumber(
    value: Double,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 28.sp
) {
    val animatedValue = remember { Animatable(0.0) }
    
    LaunchedEffect(value) {
        animatedValue.animateTo(
            targetValue = value,
            animationSpec = tween(durationMillis = 500)
        )
    }
    
    Text(
        text = formatCurrency(animatedValue.value),
        modifier = modifier,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
}

/**
 * Real-time Result Header with smooth number-counting animation
 */
@Composable
fun RealTimeResultHeader(
    uiState: CompoundInterestUiState,
    modifier: Modifier = Modifier
) {
    // Calculate result in real-time as inputs change
    val result = remember(uiState) { calculateCompoundInterest(uiState) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(AccentGreen, AccentBlue),
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Итоговая сумма",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Column {
                    // Real-time animated number
                    AnimatedNumber(
                        value = result.finalAmount,
                        fontSize = 32.sp
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Вложено: ${formatCurrency(result.totalInvested)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Доход: +${formatCurrency(result.totalInterest)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Compact input field with reduced height
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactCalculatorInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String = "",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            label,
            color = OnSurfaceMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            suffix = { 
                if (suffix.isNotEmpty()) {
                    Text(suffix, color = Primary, fontSize = 14.sp) 
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = DarkSurfaceVariant,
                focusedLabelColor = Primary,
                unfocusedLabelColor = OnSurfaceMuted,
                cursorColor = Primary
            ),
            textStyle = LocalTextStyle.current.copy(
                color = OnDarkSurface,
                fontSize = 16.sp
            ),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

/**
 * Input Screen for Compound Interest Calculator
 * Features compact design and real-time result header
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompoundInterestInputScreen(
    viewModel: CalculationViewModel = viewModel(),
    onNavigateToResult: (Double, Double, Double) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top bar with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Handle back or close */ }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = OnDarkSurface
                )
            }
            Text(
                text = "Калькулятор сложного процента",
                style = MaterialTheme.typography.titleLarge,
                color = OnDarkSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        // Real-time Result Header with animation
        RealTimeResultHeader(uiState = uiState)
        
        // Input Section - Compact Cards
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Параметры вклада",
                    color = OnDarkSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                
                CompactCalculatorInput(
                    label = "Начальный капитал",
                    value = uiState.initialCapital,
                    onValueChange = viewModel::updateInitialCapital,
                    suffix = "₽"
                )
                
                CompactCalculatorInput(
                    label = "Ежемесячное пополнение",
                    value = uiState.monthlyReplenishment,
                    onValueChange = viewModel::updateMonthlyReplenishment,
                    suffix = "₽"
                )
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Процентная ставка и срок",
                    color = OnDarkSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                
                CompactCalculatorInput(
                    label = "Ставка (% годовых)",
                    value = uiState.interestRate,
                    onValueChange = viewModel::updateInterestRate,
                    suffix = "%"
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CompactCalculatorInput(
                        label = "Лет",
                        value = uiState.termYears,
                        onValueChange = viewModel::updateTermYears,
                        modifier = Modifier.weight(1f)
                    )
                    
                    CompactCalculatorInput(
                        label = "Месяцев",
                        value = uiState.termMonths,
                        onValueChange = viewModel::updateTermMonths,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        // Compounding Frequency Selection - Compact Chips
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Частота начисления процентов",
                    color = OnDarkSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CompoundingFrequency.entries.forEach { freq ->
                        FilterChip(
                            selected = uiState.compoundingFrequency == freq,
                            onClick = { viewModel.updateCompoundingFrequency(freq) },
                            label = { 
                                Text(
                                    freq.labelRes,
                                    fontSize = 12.sp,
                                    fontWeight = if (uiState.compoundingFrequency == freq) FontWeight.Medium else FontWeight.Normal
                                ) 
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                labelColor = if (uiState.compoundingFrequency == freq) Color.White else OnSurfaceMuted
                            ),
                            border = if (uiState.compoundingFrequency == freq) null else FilterChipDefaults.filterChipBorder(
                                borderColor = DarkSurfaceVariant,
                                selectedBorderColor = Primary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
        }
        
        // Detailed Results Button
        Button(
            onClick = {
                val result = calculateCompoundInterest(uiState)
                onNavigateToResult(result.finalAmount, result.totalInvested, result.totalInterest)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Подробный расчёт",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Result Screen with detailed breakdown table
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompoundInterestResultScreen(
    finalAmount: Double,
    totalInvested: Double,
    totalInterest: Double,
    onBack: () -> Unit
) {
    val yearlyBreakdown = remember(finalAmount) { 
        // For simplicity, we'll generate a basic breakdown
        // In production, you'd pass the full result object through navigation
        listOf()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = OnDarkSurface
                )
            }
            Text(
                text = "Результаты расчёта",
                style = MaterialTheme.typography.titleLarge,
                color = OnDarkSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        // Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResultRow("Итоговая сумма", formatCurrency(finalAmount), isPrimary = true)
                Divider(color = DarkSurface, thickness = 1.dp)
                ResultRow("Всего вложено", formatCurrency(totalInvested))
                ResultRow("Заработано процентов", "+${formatCurrency(totalInterest)}", isPositive = true)
            }
        }
        
        // Yearly Breakdown Table Header
        Text(
            text = "Детализация по годам",
            color = OnDarkSurface,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
        
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceVariant, RoundedCornerShape(10.dp))
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Год", color = OnSurfaceMuted, fontSize = 11.sp, modifier = Modifier.weight(1f))
            Text("Вклад", color = OnSurfaceMuted, fontSize = 11.sp, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
            Text("%", color = OnSurfaceMuted, fontSize = 11.sp, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
            Text("Итого", color = OnSurfaceMuted, fontSize = 11.sp, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
        }
        
        // Sample breakdown rows (in production, use actual data from ViewModel)
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(5) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurface, RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${index + 1}", color = OnSurfaceMuted, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    Text(formatCurrency(totalInvested / 5), color = OnDarkSurface, fontSize = 12.sp, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                    Text("10%", color = AccentGreen, fontSize = 12.sp, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
                    Text(formatCurrency(finalAmount / 5), color = OnDarkSurface, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ResultRow(label: String, value: String, isPrimary: Boolean = false, isPositive: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = if (isPrimary) OnDarkSurface else OnSurfaceMuted,
            fontSize = if (isPrimary) 14.sp else 13.sp,
            fontWeight = if (isPrimary) FontWeight.Medium else FontWeight.Normal
        )
        Text(
            text = value,
            color = when {
                isPrimary -> Primary
                isPositive -> AccentGreen
                else -> OnDarkSurface
            },
            fontSize = if (isPrimary) 18.sp else 14.sp,
            fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Medium
        )
    }
}

/**
 * Format currency with Russian locale and ruble symbol
 */
fun formatCurrency(amount: Double): String {
    return NumberFormat.getNumberFormat(Locale("ru", "RU")).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }.format(amount) + " ₽"
}
