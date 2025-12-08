package org.example.project

actual fun currentTimeMillis(): Long {
    return kotlin.js.Date().getTime().toLong()
}

