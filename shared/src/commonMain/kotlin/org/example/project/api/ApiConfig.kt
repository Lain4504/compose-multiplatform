package org.example.project.api

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
expect object ApiConfig {
    val baseUrl: String
}

