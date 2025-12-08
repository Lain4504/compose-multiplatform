package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun CalculatorScreen() {
    val calculator = remember { Calculator() }
    var display by remember { mutableStateOf("0") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Calculator",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = display,
                    onValueChange = { display = it },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Number buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalcButton("7", modifier = Modifier.weight(1f)) { display = if (display == "0") "7" else display + "7" }
                CalcButton("8", modifier = Modifier.weight(1f)) { display = if (display == "0") "8" else display + "8" }
                CalcButton("9", modifier = Modifier.weight(1f)) { display = if (display == "0") "9" else display + "9" }
                CalcButton("C", modifier = Modifier.weight(1f), isOperator = true) {
                    display = "0"
                    errorMessage = null
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalcButton("4", modifier = Modifier.weight(1f)) { display = if (display == "0") "4" else display + "4" }
                CalcButton("5", modifier = Modifier.weight(1f)) { display = if (display == "0") "5" else display + "5" }
                CalcButton("6", modifier = Modifier.weight(1f)) { display = if (display == "0") "6" else display + "6" }
                CalcButton("+", modifier = Modifier.weight(1f), isOperator = true) { display += " + " }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalcButton("1", modifier = Modifier.weight(1f)) { display = if (display == "0") "1" else display + "1" }
                CalcButton("2", modifier = Modifier.weight(1f)) { display = if (display == "0") "2" else display + "2" }
                CalcButton("3", modifier = Modifier.weight(1f)) { display = if (display == "0") "3" else display + "3" }
                CalcButton("-", modifier = Modifier.weight(1f), isOperator = true) { display += " - " }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalcButton("0", modifier = Modifier.weight(2f)) { display = if (display == "0") "0" else display + "0" }
                CalcButton(".", modifier = Modifier.weight(1f)) { if (!display.contains(".")) display += "." }
                CalcButton("=", modifier = Modifier.weight(1f), isOperator = true) {
                    try {
                        val result = calculator.calculate(display)
                        display = if (result % 1 == 0.0) {
                            result.toInt().toString()
                        } else {
                            String.format("%.2f", result)
                        }
                        errorMessage = null
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Error"
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalcButton("*", modifier = Modifier.weight(1f), isOperator = true) { display += " * " }
                CalcButton("/", modifier = Modifier.weight(1f), isOperator = true) { display += " / " }
                CalcButton("^", modifier = Modifier.weight(1f), isOperator = true) { display += " ^ " }
                CalcButton("√", modifier = Modifier.weight(1f), isOperator = true) {
                    try {
                        val num = display.toDoubleOrNull() ?: 0.0
                        val result = calculator.sqrt(num)
                        display = if (result % 1 == 0.0) {
                            result.toInt().toString()
                        } else {
                            String.format("%.2f", result)
                        }
                        errorMessage = null
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Error"
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Example: 5 + 3 or 2 ^ 3",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CalcButton(
    text: String,
    modifier: Modifier = Modifier,
    isOperator: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isOperator) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

