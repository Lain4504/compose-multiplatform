package org.example.project

actual fun currentTimeMillis(): Long {
    return System.currentTimeMillis()
}

