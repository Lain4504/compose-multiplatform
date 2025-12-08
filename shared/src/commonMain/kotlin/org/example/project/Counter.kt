package org.example.project

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
class Counter(private var value: Int = 0) {
    fun increment(): Int {
        value++
        return value
    }
    
    fun decrement(): Int {
        value--
        return value
    }
    
    fun reset(): Int {
        value = 0
        return value
    }
    
    fun getValue(): Int = value
}

