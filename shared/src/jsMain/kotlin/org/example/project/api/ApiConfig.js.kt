package org.example.project.api

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
actual object ApiConfig {
    actual val baseUrl: String = "http://localhost:8081" // Web app localhost
}

