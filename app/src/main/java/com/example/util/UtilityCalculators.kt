package com.example.util

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.*

object ScientificCalculator {
    fun evaluate(expression: String): String {
        return try {
            val expr = expression.replace("×", "*").replace("÷", "/").replace("π", "3.14159265")
            val result = simpleEvaluate(expr)
            if (result == result.toLong().toDouble()) {
                result.toLong().toString()
            } else {
                String.format("%.6f", result).trimEnd('0').trimEnd('.')
            }
        } catch (e: Exception) {
            "Error"
        }
    }

    private fun simpleEvaluate(expr: String): Double {
        // Simple evaluator supporting +, -, *, /, sin, cos, sqrt, ^
        var cleaned = expr.trim()
        if (cleaned.startsWith("sin(")) {
            val inner = cleaned.substring(4, cleaned.length - 1).toDoubleOrNull() ?: 0.0
            return sin(Math.toRadians(inner))
        }
        if (cleaned.startsWith("cos(")) {
            val inner = cleaned.substring(4, cleaned.length - 1).toDoubleOrNull() ?: 0.0
            return cos(Math.toRadians(inner))
        }
        if (cleaned.startsWith("sqrt(")) {
            val inner = cleaned.substring(5, cleaned.length - 1).toDoubleOrNull() ?: 0.0
            return sqrt(inner)
        }

        val tokens = cleaned.split(" ")
        if (tokens.size == 3) {
            val a = tokens[0].toDoubleOrNull() ?: 0.0
            val op = tokens[1]
            val b = tokens[2].toDoubleOrNull() ?: 0.0
            return when (op) {
                "+" -> a + b
                "-" -> a - b
                "*" -> a * b
                "/" -> if (b != 0.0) a / b else Double.NaN
                "^" -> a.pow(b)
                else -> a
            }
        }
        return cleaned.toDoubleOrNull() ?: 0.0
    }
}

object UnitConverter {
    fun convertLength(value: Double, from: String, to: String): Double {
        val meters = when (from) {
            "Meters" -> value
            "Kilometers" -> value * 1000
            "Miles" -> value * 1609.34
            "Feet" -> value * 0.3048
            "Inches" -> value * 0.0254
            else -> value
        }
        return when (to) {
            "Meters" -> meters
            "Kilometers" -> meters / 1000
            "Miles" -> meters / 1609.34
            "Feet" -> meters / 0.3048
            "Inches" -> meters / 0.0254
            else -> meters
        }
    }

    fun convertWeight(value: Double, from: String, to: String): Double {
        val kg = when (from) {
            "Kilograms" -> value
            "Grams" -> value / 1000
            "Pounds" -> value * 0.453592
            "Ounces" -> value * 0.0283495
            else -> value
        }
        return when (to) {
            "Kilograms" -> kg
            "Grams" -> kg * 1000
            "Pounds" -> kg / 0.453592
            "Ounces" -> kg / 0.0283495
            else -> kg
        }
    }

    fun convertTemp(value: Double, from: String, to: String): Double {
        val celsius = when (from) {
            "Celsius" -> value
            "Fahrenheit" -> (value - 32) * 5 / 9
            "Kelvin" -> value - 273.15
            else -> value
        }
        return when (to) {
            "Celsius" -> celsius
            "Fahrenheit" -> (celsius * 9 / 5) + 32
            "Kelvin" -> celsius + 273.15
            else -> celsius
        }
    }
}

object CurrencyConverter {
    private val ratesToUSD = mapOf(
        "USD" to 1.0,
        "INR" to 83.5,
        "EUR" to 0.92,
        "GBP" to 0.78,
        "JPY" to 155.0,
        "CAD" to 1.36,
        "AUD" to 1.50
    )

    fun convert(amount: Double, from: String, to: String): Double {
        val rateFrom = ratesToUSD[from] ?: 1.0
        val rateTo = ratesToUSD[to] ?: 1.0
        val amountInUSD = amount / rateFrom
        return amountInUSD * rateTo
    }
}

object QrCodeGenerator {
    fun generateQrBitmap(content: String, size: Int = 512): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val hash = content.hashCode()
        for (x in 0 until size) {
            for (y in 0 until size) {
                // Generate a stylized geometric pattern based on content
                val isPixelOn = ((x / 16 + y / 16 + hash) % 2 == 0) ||
                        (x in 32..96 && y in 32..96) ||
                        (x in (size - 96)..(size - 32) && y in 32..96) ||
                        (x in 32..96 && y in (size - 96)..(size - 32))
                val color = if (isPixelOn) Color.parseColor("#00F0FF") else Color.parseColor("#0F172A")
                bitmap.setPixel(x, y, color)
            }
        }
        return bitmap
    }
}
