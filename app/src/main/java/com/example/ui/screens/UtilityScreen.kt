package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NovaXViewModel
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.*
import com.example.util.CurrencyConverter
import com.example.util.QrCodeGenerator
import com.example.util.ScientificCalculator
import com.example.util.UnitConverter

@Composable
fun UtilityScreen(viewModel: NovaXViewModel) {
    var selectedUtil by remember { mutableIntStateOf(0) }
    val utils = listOf("🧮 Calculator", "📏 Units", "💱 Currency", "📱 QR Gen", "🌤️ Weather & News")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text("Utilities & Media Suite", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
        Spacer(modifier = Modifier.height(12.dp))

        ScrollableTabRow(
            selectedTabIndex = selectedUtil,
            containerColor = Color.Transparent,
            contentColor = NeonCyan,
            edgePadding = 0.dp
        ) {
            utils.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedUtil == idx,
                    onClick = { selectedUtil = idx },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedUtil == idx) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedUtil == idx) NeonCyan else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedUtil) {
            0 -> CalculatorSection()
            1 -> UnitConverterSection()
            2 -> CurrencyConverterSection()
            3 -> QrGeneratorSection()
            4 -> WeatherNewsSection()
        }
    }
}

@Composable
fun CalculatorSection() {
    var expr by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }

    val buttons = listOf(
        "C", "sin(", "cos(", "÷",
        "7", "8", "9", "×",
        "4", "5", "6", "-",
        "1", "2", "3", "+",
        "0", ".", "sqrt(", "="
    )

    Column(modifier = Modifier.fillMaxSize()) {
        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = expr.ifEmpty { "0" },
                    fontSize = 20.sp,
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = result,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(buttons) { btn ->
                GlassmorphicCard(
                    modifier = Modifier
                        .height(60.dp)
                        .clickable {
                            when (btn) {
                                "C" -> {
                                    expr = ""
                                    result = "0"
                                }
                                "=" -> {
                                    result = ScientificCalculator.evaluate(expr)
                                }
                                else -> {
                                    expr += btn
                                }
                            }
                        }
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = btn,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (btn == "=" || btn == "C") NeonCyan else TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UnitConverterSection() {
    var inputValue by remember { mutableStateOf("10") }
    var selectedUnitType by remember { mutableStateOf("Length") }
    var fromUnit by remember { mutableStateOf("Meters") }
    var toUnit by remember { mutableStateOf("Feet") }

    val valDouble = inputValue.toDoubleOrNull() ?: 0.0
    val convertedVal = when (selectedUnitType) {
        "Length" -> UnitConverter.convertLength(valDouble, fromUnit, toUnit)
        "Weight" -> UnitConverter.convertWeight(valDouble, fromUnit, toUnit)
        else -> UnitConverter.convertTemp(valDouble, fromUnit, toUnit)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Length", "Weight", "Temp").forEach { type ->
                FilterChip(
                    selected = selectedUnitType == type,
                    onClick = {
                        selectedUnitType = type
                        if (type == "Length") { fromUnit = "Meters"; toUnit = "Feet" }
                        if (type == "Weight") { fromUnit = "Kilograms"; toUnit = "Pounds" }
                        if (type == "Temp") { fromUnit = "Celsius"; toUnit = "Fahrenheit" }
                    },
                    label = { Text(type) }
                )
            }
        }

        OutlinedTextField(
            value = inputValue,
            onValueChange = { inputValue = it },
            label = { Text("Input Value") },
            modifier = Modifier.fillMaxWidth()
        )

        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Result:", color = TextSecondary, fontSize = 12.sp)
                Text(
                    text = String.format("%.2f %s = %.2f %s", valDouble, fromUnit, convertedVal, toUnit),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
            }
        }
    }
}

@Composable
fun CurrencyConverterSection() {
    var amountText by remember { mutableStateOf("100") }
    var fromCurr by remember { mutableStateOf("USD") }
    var toCurr by remember { mutableStateOf("INR") }

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val converted = CurrencyConverter.convert(amount, fromCurr, toCurr)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            label = { Text("Amount ($fromCurr)") },
            modifier = Modifier.fillMaxWidth()
        )

        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Exchange Rate Calculation:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$amount $fromCurr = ${String.format("%.2f", converted)} $toCurr",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
            }
        }
    }
}

@Composable
fun QrGeneratorSection() {
    var qrContent by remember { mutableStateOf("https://novax.ai") }
    val qrBitmap = remember(qrContent) { QrCodeGenerator.generateQrBitmap(qrContent) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = qrContent,
            onValueChange = { qrContent = it },
            label = { Text("Enter text or URL for QR Code") },
            modifier = Modifier.fillMaxWidth()
        )

        GlassmorphicCard(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun WeatherNewsSection() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("New Delhi, IN", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Clear Sky • Humidity 45%", fontSize = 12.sp, color = TextSecondary)
                        }
                        Text("31°C", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    }
                }
            }
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Latest AI & Tech Digest", fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("1. Quantum AI Chipset achieves 10x lower latency in edge robotics.", fontSize = 13.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("2. Next-gen neural models support instant multimodal voice synthesis.", fontSize = 13.sp, color = TextPrimary)
                }
            }
        }
    }
}
