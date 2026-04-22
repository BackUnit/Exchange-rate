package com.example.financecalculators.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financecalculators.data.api.CurrencyApi
import com.example.financecalculators.data.model.CurrencyRate
import com.example.financecalculators.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var showMenu by remember { mutableStateOf(false) }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    
    Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            when(currentScreen) {
                                Screen.Home -> "Финансовые Калькуляторы"
                                Screen.CompoundInterest -> "Сложный процент"
                                Screen.Loan -> "Кредитный калькулятор"
                                Screen.Currency -> "Конвертер валют"
                                Screen.Settings -> "Настройки"
                                Screen.Support -> "Поддержка"
                                Screen.About -> "О приложении"
                            },
                            color = OnDarkSurface,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkSurface,
                        titleContentColor = OnDarkSurface
                    ),
                    navigationIcon = {
                        if (currentScreen != Screen.Home) {
                            IconButton(onClick = { currentScreen = Screen.Home }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = OnDarkSurface)
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Меню", tint = OnDarkSurface)
                        }
                    }
                )
            },
            containerColor = DarkBackground
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                when (currentScreen) {
                    Screen.Home -> HomeContent(onNavigate = { currentScreen = it })
                    Screen.CompoundInterest -> CompoundInterestCalculator()
                    Screen.Loan -> LoanCalculator()
                    Screen.Currency -> CurrencyConverter()
                    Screen.Settings -> SettingsScreen()
                    Screen.Support -> SupportScreen()
                    Screen.About -> AboutScreen()
                }
            }
        }
        
        DrawerMenu(
            showMenu = showMenu,
            onDismiss = { showMenu = false },
            onNavigate = { screen ->
                currentScreen = screen
                showMenu = false
            }
        )
    }
}

enum class Screen {
    Home,
    CompoundInterest,
    Loan,
    Currency,
    Settings,
    Support,
    About
}

@Composable
fun DrawerMenu(
    showMenu: Boolean,
    onDismiss: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    AnimatedVisibility(
        visible = showMenu,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(onClick = onDismiss)
        ) {
            AnimatedVisibility(
                visible = showMenu,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it })
            ) {
                Surface(
                    modifier = Modifier
                        .width(280.dp)
                        .fillMaxHeight(),
                    color = DarkSurface,
                    shape = RoundedCornerShape(bottomEnd = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Spacer(modifier = Modifier.height(60.dp))
                        
                        Text(
                            text = "Меню",
                            style = MaterialTheme.typography.titleLarge,
                            color = Primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        MenuItem("📊 Сложный процент", onClick = { onNavigate(Screen.CompoundInterest) })
                        MenuItem("💳 Кредитный калькулятор", onClick = { onNavigate(Screen.Loan) })
                        MenuItem("💱 Конвертер валют", onClick = { onNavigate(Screen.Currency) })
                        
                        Divider(color = DarkSurfaceVariant, thickness = 1.dp)
                        
                        MenuItem("⚙️ Настройки", onClick = { onNavigate(Screen.Settings) })
                        MenuItem("❓ Поддержка", onClick = { onNavigate(Screen.Support) })
                        MenuItem("ℹ️ О приложении", onClick = { onNavigate(Screen.About) })
                    }
                }
            }
        }
    }
}

@Composable
fun MenuItem(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        color = DarkSurfaceVariant,
        onClick = onClick
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            color = OnDarkSurface,
            fontSize = 16.sp
        )
    }
}

@Composable
fun HomeContent(onNavigate: (Screen) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CalculatorCard(
                title = "Сложный процент",
                description = "Рассчитайте доходность ваших инвестиций с учетом сложного процента",
                icon = Icons.Default.TrendingUp,
                gradientColors = listOf(AccentGreen, AccentBlue),
                onClick = { onNavigate(Screen.CompoundInterest) }
            )
        }
        
        item {
            CalculatorCard(
                title = "Кредитный калькулятор",
                description = "Рассчитайте ежемесячный платеж и общую сумму кредита",
                icon = Icons.Default.CreditCard,
                gradientColors = listOf(AccentOrange, AccentPurple),
                onClick = { onNavigate(Screen.Loan) }
            )
        }
        
        item {
            CalculatorCard(
                title = "Конвертер валют",
                description = "Конвертируйте валюты по актуальному курсу",
                icon = Icons.Default.Refresh,
                gradientColors = listOf(AccentPurple, AccentBlue),
                onClick = { onNavigate(Screen.Currency) }
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun CalculatorCard(
    title: String,
    description: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(20.dp)),
        onClick = onClick
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
                .clickable { onClick() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}
