package com.example.financecalculators.ui.screens.compoundinterest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financecalculators.R
import com.example.financecalculators.ui.theme.*
import com.example.financecalculators.viewmodel.CompoundInterestViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompoundInterestScreen(viewModel: CompoundInterestViewModel = viewModel()) {
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
        
        CompoundInterestInput(
            label = stringResource(R.string.ci_label_principal),
            value = principal,
            onValueChange = { viewModel.updatePrincipal(it) },
            suffix = stringResource(R.string.ci_suffix_rubles)
        )
        
        CompoundInterestInput(
            label = stringResource(R.string.ci_label_rate),
            value = rate,
            onValueChange = { viewModel.updateRate(it) },
            suffix = stringResource(R.string.ci_suffix_percent)
        )
        
        CompoundInterestInput(
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
        
        CompoundInterestResultCard(
            title = stringResource(R.string.ci_result_title),
            value = formatCurrency(result),
            subtitle = stringResource(R.string.ci_result_subtitle, formatCurrency(interest)),
            backgroundColor = AccentGreen
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompoundInterestInput(
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
fun CompoundInterestResultCard(
    title: String,
    value: String,
    subtitle: String,
    backgroundColor: Color
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
                .background(SolidColor(backgroundColor))
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
    val formatter = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    formatter.maximumFractionDigits = 2
    formatter.minimumFractionDigits = 2
    return formatter.format(amount)
}
