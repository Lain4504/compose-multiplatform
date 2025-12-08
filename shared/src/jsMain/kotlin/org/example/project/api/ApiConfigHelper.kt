package org.example.project.api

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Helper function to get base URL for JS
 * Since expect/actual objects may not export properly, use a function instead
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
fun getApiBaseUrl(): String {
    return ApiConfig.baseUrl
}

