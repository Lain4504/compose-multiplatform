package org.example.project.api

import io.ktor.client.HttpClient
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.Promise
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

/**
 * JS-specific wrapper for TaskApi that returns Promises instead of suspend functions
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
class TaskApiJs(
    private val taskApi: TaskApi
) {
    fun getAllTasks(): Promise<Array<TaskDto>> {
        return GlobalScope.promise {
            taskApi.getAllTasks().toTypedArray()
        }
    }
    
    fun getTask(id: String): Promise<TaskDto?> {
        return GlobalScope.promise {
            taskApi.getTask(id)
        }
    }
    
    fun createTask(task: Any): Promise<TaskDto> {
        return GlobalScope.promise {
            try {
                // Convert JavaScript object to TaskDto
                val taskObj = task.asDynamic()
                val taskDto = TaskDto(
                    id = taskObj.id as? String,
                    title = taskObj.title as String,
                    description = (taskObj.description as? String) ?: "",
                    isCompleted = (taskObj.isCompleted as? Boolean) ?: false,
                    createdAt = taskObj.createdAt as? Long
                )
                taskApi.createTask(taskDto)
            } catch (e: Throwable) {
                console.error("Error creating task:", e)
                throw e
            }
        }
    }
    
    fun updateTask(id: String, task: Any): Promise<TaskDto?> {
        return GlobalScope.promise {
            // Convert JavaScript object to TaskDto
            val taskObj = task.asDynamic()
            val taskDto = TaskDto(
                id = taskObj.id as? String,
                title = taskObj.title as String,
                description = (taskObj.description as? String) ?: "",
                isCompleted = (taskObj.isCompleted as? Boolean) ?: false,
                createdAt = taskObj.createdAt as? Long
            )
            taskApi.updateTask(id, taskDto)
        }
    }
    
    fun deleteTask(id: String): Promise<Boolean> {
        return GlobalScope.promise {
            taskApi.deleteTask(id)
        }
    }
}

@OptIn(ExperimentalJsExport::class)
@JsExport
fun createTaskApiJs(baseUrl: String, httpClient: Any): TaskApiJs {
    @Suppress("UNCHECKED_CAST")
    val client = httpClient as HttpClient
    val taskApi = TaskApiImpl(baseUrl, client)
    return TaskApiJs(taskApi)
}

