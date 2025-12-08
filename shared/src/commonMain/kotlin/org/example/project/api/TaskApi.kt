package org.example.project.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlinx.serialization.json.Json

interface TaskApi {
    suspend fun getAllTasks(): List<TaskDto>
    suspend fun getTask(id: String): TaskDto?
    suspend fun createTask(task: TaskDto): TaskDto
    suspend fun updateTask(id: String, task: TaskDto): TaskDto?
    suspend fun deleteTask(id: String): Boolean
}

class TaskApiImpl(
    private val baseUrl: String,
    private val httpClient: HttpClient
) : TaskApi {
    
    companion object {
        const val TASKS_ENDPOINT = "/api/tasks"
    }
    
    override suspend fun getAllTasks(): List<TaskDto> {
        val response = httpClient.get("$baseUrl$TASKS_ENDPOINT") {
            contentType(ContentType.Application.Json)
        }
        return response.body<TaskListResponse>().tasks
    }
    
    override suspend fun getTask(id: String): TaskDto? {
        return try {
            val response = httpClient.get("$baseUrl$TASKS_ENDPOINT/$id") {
                contentType(ContentType.Application.Json)
            }
            response.body<TaskResponse>().task
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun createTask(task: TaskDto): TaskDto {
        val response = httpClient.post("$baseUrl$TASKS_ENDPOINT") {
            contentType(ContentType.Application.Json)
            setBody(task)
        }
        return response.body<TaskResponse>().task
    }
    
    override suspend fun updateTask(id: String, task: TaskDto): TaskDto? {
        return try {
            val response = httpClient.put("$baseUrl$TASKS_ENDPOINT/$id") {
                contentType(ContentType.Application.Json)
                setBody(task)
            }
            response.body<TaskResponse>().task
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun deleteTask(id: String): Boolean {
        return try {
            val response = httpClient.delete("$baseUrl$TASKS_ENDPOINT/$id")
            response.status == HttpStatusCode.OK || response.status == HttpStatusCode.NoContent
        } catch (e: Exception) {
            false
        }
    }
}

@OptIn(ExperimentalJsExport::class)
@JsExport
fun createHttpClient(): HttpClient {
    return HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = false
            })
        }
    }
}

