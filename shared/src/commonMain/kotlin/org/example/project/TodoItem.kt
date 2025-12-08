package org.example.project

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
data class TodoItem(
    val id: String,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = 0L
) {
    companion object {
        fun create(
            id: String,
            title: String,
            description: String = "",
            isCompleted: Boolean = false
        ): TodoItem {
            return TodoItem(
                id = id,
                title = title,
                description = description,
                isCompleted = isCompleted,
                createdAt = currentTimeMillis()
            )
        }
    }
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class TodoManager {
    private val todos = mutableListOf<TodoItem>()
    
    fun addTodo(title: String, description: String = ""): TodoItem {
        val todo = TodoItem.create(
            id = "${currentTimeMillis()}-${todos.size}",
            title = title,
            description = description
        )
        todos.add(todo)
        return todo
    }
    
    fun removeTodo(id: String): Boolean {
        return todos.removeIf { todo -> todo.id == id }
    }
    
    fun toggleTodo(id: String): TodoItem? {
        val index = todos.indexOfFirst { it.id == id }
        return if (index != -1) {
            val todo = todos[index]
            val updated = todo.copy(isCompleted = !todo.isCompleted)
            todos[index] = updated
            updated
        } else {
            null
        }
    }
    
    fun getAllTodos(): List<TodoItem> = todos.toList()
    
    fun getCompletedTodos(): List<TodoItem> = todos.filter { it.isCompleted }
    
    fun getPendingTodos(): List<TodoItem> = todos.filter { !it.isCompleted }
    
    fun clearCompleted(): Int {
        val count = todos.size
        todos.removeAll { it.isCompleted }
        return count - todos.size
    }
}

