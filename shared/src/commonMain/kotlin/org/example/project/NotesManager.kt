package org.example.project

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
data class Note(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val color: String = "#FFFFFF"
) {
    companion object {
        fun create(
            id: String,
            title: String,
            content: String,
            color: String = "#FFFFFF"
        ): Note {
            val now = currentTimeMillis()
            return Note(
                id = id,
                title = title,
                content = content,
                createdAt = now,
                updatedAt = now,
                color = color
            )
        }
    }
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class NotesManager {
    private val notes = mutableListOf<Note>()
    
    fun addNote(title: String, content: String, color: String = "#FFFFFF"): Note {
        val note = Note.create(
            id = "${currentTimeMillis()}-${notes.size}",
            title = title,
            content = content,
            color = color
        )
        notes.add(note)
        return note
    }
    
    fun updateNote(id: String, title: String, content: String, color: String? = null): Note? {
        val index = notes.indexOfFirst { note -> note.id == id }
        return if (index != -1) {
            val existing = notes[index]
            val updated = existing.copy(
                title = title,
                content = content,
                color = color ?: existing.color,
                updatedAt = currentTimeMillis()
            )
            notes[index] = updated
            updated
        } else {
            null
        }
    }
    
    fun deleteNote(id: String): Boolean {
        val iterator = notes.iterator()
        var removed = false
        while (iterator.hasNext()) {
            val note = iterator.next()
            if (note.id == id) {
                iterator.remove()
                removed = true
            }
        }
        return removed
    }
    
    fun getNote(id: String): Note? {
        return notes.find { it.id == id }
    }
    
    fun getAllNotes(): List<Note> = notes.toList().sortedByDescending { it.updatedAt }
    
    fun searchNotes(query: String): List<Note> {
        val lowerQuery = query.lowercase()
        return notes.filter {
            it.title.lowercase().contains(lowerQuery) ||
            it.content.lowercase().contains(lowerQuery)
        }
    }
}

