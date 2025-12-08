package org.example.project

actual fun formatString(format: String, vararg args: Any): String {
    return String.format(format, *args)
}

