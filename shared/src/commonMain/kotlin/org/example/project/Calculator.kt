package org.example.project

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
class Calculator {
    fun add(a: Double, b: Double): Double = a + b
    
    fun subtract(a: Double, b: Double): Double = a - b
    
    fun multiply(a: Double, b: Double): Double = a * b
    
    fun divide(a: Double, b: Double): Double {
        if (b == 0.0) throw IllegalArgumentException("Cannot divide by zero")
        return a / b
    }
    
    fun power(base: Double, exponent: Double): Double {
        return kotlin.math.pow(base, exponent)
    }
    
    fun sqrt(value: Double): Double {
        if (value < 0) throw IllegalArgumentException("Cannot calculate square root of negative number")
        return kotlin.math.sqrt(value)
    }
    
    fun calculate(expression: String): Double {
        // Simple expression evaluator (supports basic operations)
        val parts = expression.split(" ")
        if (parts.size != 3) throw IllegalArgumentException("Invalid expression format. Use: 'number operator number'")
        
        val a = parts[0].toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: ${parts[0]}")
        val operator = parts[1]
        val b = parts[2].toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: ${parts[2]}")
        
        return when (operator) {
            "+" -> add(a, b)
            "-" -> subtract(a, b)
            "*" -> multiply(a, b)
            "/" -> divide(a, b)
            "^" -> power(a, b)
            else -> throw IllegalArgumentException("Unsupported operator: $operator")
        }
    }
}

