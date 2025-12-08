package org.example.project.api

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlinx.serialization.Serializable

@OptIn(ExperimentalJsExport::class)
@JsExport

@Serializable
data class TaskDto(
    val id: String? = null,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long? = null
)

@Serializable
data class TaskListResponse(
    val tasks: List<TaskDto>
)

@Serializable
data class TaskResponse(
    val task: TaskDto
)

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String
)

