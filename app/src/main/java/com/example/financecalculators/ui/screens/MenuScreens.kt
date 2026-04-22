package com.example.financecalculators.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financecalculators.ui.theme.*

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SettingsCategory("Основные")
        SettingsItem("🌙 Тема", "Тёмная", onClick = {})
        SettingsItem("🔔 Уведомления", "Включены", onClick = {})
        SettingsItem("🌐 Язык", "Русский", onClick = {})
        
        Divider(color = DarkSurfaceVariant, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        
        SettingsCategory("Калькуляторы")
        SettingsItem("📊 Точность расчётов", "2 знака", onClick = {})
        SettingsItem("💾 Сохранять историю", "Да", onClick = {})
        SettingsItem("🔄 Автообновление курсов", "Каждый час", onClick = {})
        
        Divider(color = DarkSurfaceVariant, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        
        SettingsCategory("Безопасность")
        SettingsItem("🔒 Код доступа", "Не установлен", onClick = {})
        SettingsItem("👆 Биометрия", "Отключена", onClick = {})
    }
}

@Composable
fun SettingsCategory(name: String) {
    Text(
        text = name,
        color = Primary,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        modifier = Modifier.padding(start = 4.dp, top = 8.dp)
    )
}

@Composable
fun SettingsItem(title: String, value: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        color = DarkSurfaceVariant,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = OnDarkSurface,
                fontSize = 16.sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = value,
                    color = OnDarkSurface.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Text(text = "›", color = Primary, fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun SupportScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InfoCard(
            title = "Частые вопросы",
            items = listOf(
                "Как рассчитывается сложный процент?",
                "Можно ли экспортировать результаты?",
                "Как часто обновляются курсы валют?",
                "Безопасны ли мои данные?"
            ),
            gradientColors = listOf(AccentBlue, AccentPurple)
        )
        
        InfoCard(
            title = "Связаться с поддержкой",
            items = listOf(
                "📧 support@financecalc.com",
                "📱 +7 (999) 123-45-67",
                "💬 Онлайн чат доступен с 9:00 до 21:00"
            ),
            gradientColors = listOf(AccentGreen, AccentBlue)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Отправить отзыв",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnDarkSurface,
                    fontWeight = FontWeight.Bold
                )
                
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Ваше сообщение", color = OnDarkSurface.copy(alpha = 0.7f)) },
                    minLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = DarkSurfaceVariant
                    )
                )
                
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Отправить", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(AccentPurple, AccentBlue))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "FC",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Финансовые Калькуляторы",
                style = MaterialTheme.typography.headlineSmall,
                color = OnDarkSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Версия 1.0.0",
                color = OnDarkSurface.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AboutItem("👨‍💻 Разработчик", "Finance Team")
                AboutItem("📅 Год выпуска", "2024")
                AboutItem("🌐 Сайт", "financecalc.com")
                AboutItem("📄 Лицензия", "MIT")
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "О приложении",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnDarkSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Приложение предоставляет инструменты для финансовых расчётов: калькулятор сложного процента, кредитный калькулятор и конвертер валют с актуальными курсами.",
                    color = OnDarkSurface.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
        
        Text(
            text = "© 2024 Finance Calculators. Все права защищены.",
            color = OnDarkSurface.copy(alpha = 0.5f),
            fontSize = 12.sp
        )
    }
}

@Composable
fun AboutItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = OnDarkSurface.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = OnDarkSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun InfoCard(
    title: String,
    items: List<String>,
    gradientColors: List<Color>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                items.forEach { item ->
                    Text(
                        text = item,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
