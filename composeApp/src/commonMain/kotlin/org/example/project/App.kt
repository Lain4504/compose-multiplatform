package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Compose Multiplatform Demo") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                NavigationBar(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NavigationBarItem(
                        selected = currentScreen == Screen.Home,
                        onClick = { currentScreen = Screen.Home },
                        icon = { Text("🏠") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.Counter,
                        onClick = { currentScreen = Screen.Counter },
                        icon = { Text("🔢") },
                        label = { Text("Counter") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.TodoList,
                        onClick = { currentScreen = Screen.TodoList },
                        icon = { Text("✅") },
                        label = { Text("Todos") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.Calculator,
                        onClick = { currentScreen = Screen.Calculator },
                        icon = { Text("🧮") },
                        label = { Text("Calc") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.Timer,
                        onClick = { currentScreen = Screen.Timer },
                        icon = { Text("⏱️") },
                        label = { Text("Timer") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.Notes,
                        onClick = { currentScreen = Screen.Notes },
                        icon = { Text("📝") },
                        label = { Text("Notes") }
                    )
                }
                
                when (currentScreen) {
                    Screen.Home -> HomeScreen()
                    Screen.Counter -> CounterScreen()
                    Screen.TodoList -> TodoListScreen()
                    Screen.Calculator -> CalculatorScreen()
                    Screen.Timer -> TimerScreen()
                    Screen.Notes -> NotesScreen()
                }
            }
        }
    }
}

sealed class Screen {
    object Home : Screen()
    object Counter : Screen()
    object TodoList : Screen()
    object Calculator : Screen()
    object Timer : Screen()
    object Notes : Screen()
}

