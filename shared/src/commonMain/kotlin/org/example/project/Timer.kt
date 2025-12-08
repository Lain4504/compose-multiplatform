package org.example.project

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
class Timer {
    private var startTime: Long = 0
    private var pausedTime: Long = 0
    private var isRunning: Boolean = false
    private var isPaused: Boolean = false
    
    fun start() {
        if (!isRunning && !isPaused) {
            startTime = currentTimeMillis()
            isRunning = true
        } else if (isPaused) {
            // Resume from paused time
            startTime = currentTimeMillis() - pausedTime
            isPaused = false
            isRunning = true
        }
    }
    
    fun pause() {
        if (isRunning) {
            pausedTime = getElapsedTime()
            isRunning = false
            isPaused = true
        }
    }
    
    fun reset() {
        startTime = 0
        pausedTime = 0
        isRunning = false
        isPaused = false
    }
    
    fun getElapsedTime(): Long {
        return if (isRunning) {
            currentTimeMillis() - startTime
        } else if (isPaused) {
            pausedTime
        } else {
            0
        }
    }
    
    fun getElapsedSeconds(): Long {
        return getElapsedTime() / 1000
    }
    
    fun getElapsedMinutes(): Long {
        return getElapsedSeconds() / 60
    }
    
    fun getElapsedHours(): Long {
        return getElapsedMinutes() / 60
    }
    
    fun formatTime(): String {
        val totalSeconds = getElapsedSeconds()
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
    
    fun getStatus(): String {
        return when {
            isRunning -> "Running"
            isPaused -> "Paused"
            else -> "Stopped"
        }
    }
}

