package org.example.project.repository

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import org.example.project.api.TaskApi
import org.example.project.api.TaskDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface TaskRepository {
    val tasks: StateFlow<List<TaskDto>>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>
    
    suspend fun loadTasks()
    suspend fun getTask(id: String): TaskDto?
    suspend fun createTask(title: String, description: String): Result<TaskDto>
    suspend fun updateTask(id: String, title: String, description: String, isCompleted: Boolean): Result<TaskDto>
    suspend fun deleteTask(id: String): Result<Unit>
    suspend fun toggleTask(id: String): Result<TaskDto>
}

class TaskRepositoryImpl(
    private val taskApi: TaskApi
) : TaskRepository {
    
    private val _tasks = MutableStateFlow<List<TaskDto>>(emptyList())
    override val tasks: StateFlow<List<TaskDto>> = _tasks.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()
    
    override suspend fun loadTasks() {
        _isLoading.value = true
        _error.value = null
        try {
            val fetchedTasks = taskApi.getAllTasks()
            _tasks.value = fetchedTasks
        } catch (e: Exception) {
            _error.value = "Failed to load tasks: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
    
    override suspend fun getTask(id: String): TaskDto? {
        return try {
            taskApi.getTask(id)
        } catch (e: Exception) {
            _error.value = "Failed to get task: ${e.message}"
            null
        }
    }
    
    override suspend fun createTask(title: String, description: String): Result<TaskDto> {
        _isLoading.value = true
        _error.value = null
        return try {
            val newTask = TaskDto(
                title = title,
                description = description,
                isCompleted = false
            )
            val createdTask = taskApi.createTask(newTask)
            _tasks.value = _tasks.value + createdTask
            Result.success(createdTask)
        } catch (e: Exception) {
            val errorMsg = "Failed to create task: ${e.message}"
            _error.value = errorMsg
            Result.failure(Exception(errorMsg))
        } finally {
            _isLoading.value = false
        }
    }
    
    override suspend fun updateTask(
        id: String,
        title: String,
        description: String,
        isCompleted: Boolean
    ): Result<TaskDto> {
        _isLoading.value = true
        _error.value = null
        return try {
            val existingTask = _tasks.value.find { it.id == id }
            if (existingTask == null) {
                val errorMsg = "Task not found"
                _error.value = errorMsg
                return Result.failure(Exception(errorMsg))
            }
            
            val updatedTask = TaskDto(
                id = id,
                title = title,
                description = description,
                isCompleted = isCompleted,
                createdAt = existingTask.createdAt
            )
            
            val result = taskApi.updateTask(id, updatedTask)
            if (result != null) {
                _tasks.value = _tasks.value.map { if (it.id == id) result else it }
                Result.success(result)
            } else {
                val errorMsg = "Failed to update task"
                _error.value = errorMsg
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            val errorMsg = "Failed to update task: ${e.message}"
            _error.value = errorMsg
            Result.failure(Exception(errorMsg))
        } finally {
            _isLoading.value = false
        }
    }
    
    override suspend fun deleteTask(id: String): Result<Unit> {
        _isLoading.value = true
        _error.value = null
        return try {
            val success = taskApi.deleteTask(id)
            if (success) {
                _tasks.value = _tasks.value.filter { it.id != id }
                Result.success(Unit)
            } else {
                val errorMsg = "Failed to delete task"
                _error.value = errorMsg
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            val errorMsg = "Failed to delete task: ${e.message}"
            _error.value = errorMsg
            Result.failure(Exception(errorMsg))
        } finally {
            _isLoading.value = false
        }
    }
    
    override suspend fun toggleTask(id: String): Result<TaskDto> {
        val existingTask = _tasks.value.find { it.id == id }
        if (existingTask == null) {
            val errorMsg = "Task not found"
            _error.value = errorMsg
            return Result.failure(Exception(errorMsg))
        }
        
        return updateTask(
            id = id,
            title = existingTask.title,
            description = existingTask.description,
            isCompleted = !existingTask.isCompleted
        )
    }
}

