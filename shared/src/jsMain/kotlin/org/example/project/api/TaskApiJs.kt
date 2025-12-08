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
    
    fun createTask(task: TaskDto): Promise<TaskDto> {
        return GlobalScope.promise {
            taskApi.createTask(task)
        }
    }
    
    fun updateTask(id: String, task: TaskDto): Promise<TaskDto?> {
        return GlobalScope.promise {
            taskApi.updateTask(id, task)
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

