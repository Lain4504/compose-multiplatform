package org.example.project

/**
 * Format string with placeholders
 * Works across all platforms in Kotlin Multiplatform
 */
expect fun formatString(format: String, vararg args: Any): String

