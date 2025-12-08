package org.example.project.repository

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import org.example.project.api.TaskApi
import org.example.project.api.TaskDto

/**
 * Helper class to expose repository functionality in a way that's easier to use from Swift/JS
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
class TaskRepositoryHelper(
    private val repository: TaskRepository
) {
    suspend fun loadTasks() = repository.loadTasks()
    suspend fun getTask(id: String) = repository.getTask(id)
    suspend fun createTask(title: String, description: String) = repository.createTask(title, description)
    suspend fun updateTask(id: String, title: String, description: String, isCompleted: Boolean) = 
        repository.updateTask(id, title, description, isCompleted)
    suspend fun deleteTask(id: String) = repository.deleteTask(id)
    suspend fun toggleTask(id: String) = repository.toggleTask(id)
    
    fun getTasksFlow() = repository.tasks
    fun getLoadingFlow() = repository.isLoading
    fun getErrorFlow() = repository.error
}

