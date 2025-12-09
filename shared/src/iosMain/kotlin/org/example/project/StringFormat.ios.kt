package org.example.project

actual fun formatString(format: String, vararg args: Any): String {
    // Simple formatter for iOS - supports %02d format
    var result = format
    var argIndex = 0
    while (argIndex < args.size && result.contains("%")) {
        val arg = args[argIndex]
        val value = when {
            result.contains("%02d") -> {
                val num = when (arg) {
                    is Long -> arg.toInt()
                    is Int -> arg
                    else -> arg.toString().toIntOrNull() ?: 0
                }
                num.toString().padStart(2, '0')
            }
            result.contains("%d") -> {
                when (arg) {
                    is Long -> arg.toString()
                    is Int -> arg.toString()
                    else -> arg.toString()
                }
            }
            else -> arg.toString()
        }
        result = when {
            result.contains("%02d") -> result.replaceFirst("%02d", value)
            result.contains("%d") -> result.replaceFirst("%d", value)
            result.contains("%s") -> result.replaceFirst("%s", value)
            else -> result
        }
        argIndex++
    }
    return result
}

