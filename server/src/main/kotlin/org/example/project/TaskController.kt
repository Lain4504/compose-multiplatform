package org.example.project

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.api.*
import java.util.*

@kotlinx.serialization.Serializable
data class ErrorResponse(
    val error: String,
    val message: String
)

// In-memory storage (replace with database in production)
private val tasks = mutableListOf<TaskDto>()

fun Application.configureTaskRoutes() {
    routing {
        route("/api/tasks") {
            // GET /api/tasks - Get all tasks
            get {
                call.respond(TaskListResponse(tasks = tasks.sortedByDescending { it.createdAt ?: 0L }))
            }
            
            // GET /api/tasks/{id} - Get task by ID
            get("{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("BAD_REQUEST", "Task ID is required")
                )
                
                val task = tasks.find { it.id == id }
                if (task != null) {
                    call.respond(TaskResponse(task = task))
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse("NOT_FOUND", "Task with ID $id not found")
                    )
                }
            }
            
            // POST /api/tasks - Create new task
            post {
                try {
                    val taskRequest = call.receive<TaskDto>()
                    
                    if (taskRequest.title.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("BAD_REQUEST", "Task title is required")
                        )
                        return@post
                    }
                    
                    val newTask = TaskDto(
                        id = UUID.randomUUID().toString(),
                        title = taskRequest.title,
                        description = taskRequest.description,
                        isCompleted = taskRequest.isCompleted,
                        createdAt = org.example.project.currentTimeMillis()
                    )
                    
                    tasks.add(newTask)
                    call.respond(HttpStatusCode.Created, TaskResponse(task = newTask))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("BAD_REQUEST", "Invalid request: ${e.message}")
                    )
                }
            }
            
            // PUT /api/tasks/{id} - Update task
            put("{id}") {
                val id = call.parameters["id"] ?: return@put call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("BAD_REQUEST", "Task ID is required")
                )
                
                try {
                    val taskRequest = call.receive<TaskDto>()
                    val existingIndex = tasks.indexOfFirst { it.id == id }
                    
                    if (existingIndex == -1) {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ErrorResponse("NOT_FOUND", "Task with ID $id not found")
                        )
                        return@put
                    }
                    
                    if (taskRequest.title.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("BAD_REQUEST", "Task title is required")
                        )
                        return@put
                    }
                    
                    val updatedTask = TaskDto(
                        id = id,
                        title = taskRequest.title,
                        description = taskRequest.description,
                        isCompleted = taskRequest.isCompleted,
                        createdAt = tasks[existingIndex].createdAt
                    )
                    
                    tasks[existingIndex] = updatedTask
                    call.respond(TaskResponse(task = updatedTask))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("BAD_REQUEST", "Invalid request: ${e.message}")
                    )
                }
            }
            
            // DELETE /api/tasks/{id} - Delete task
            delete("{id}") {
                val id = call.parameters["id"] ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("BAD_REQUEST", "Task ID is required")
                )
                
                val removed = tasks.removeIf { it.id == id }
                if (removed) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse("NOT_FOUND", "Task with ID $id not found")
                    )
                }
            }
        }
    }
}

