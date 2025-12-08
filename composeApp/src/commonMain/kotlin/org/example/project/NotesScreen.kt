package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.border
import org.jetbrains.compose.ui.tooling.preview.Preview

fun parseHexColor(hex: String): Color {
    val cleanHex = hex.removePrefix("#")
    val colorInt = cleanHex.toLongOrNull(16) ?: 0xFFFFFF
    val r = ((colorInt shr 16) and 0xFF).toInt()
    val g = ((colorInt shr 8) and 0xFF).toInt()
    val b = (colorInt and 0xFF).toInt()
    return Color(r, g, b)
}

@Composable
@Preview
fun NotesScreen() {
    val notesManager = remember { NotesManager() }
    var notes by remember { mutableStateOf(notesManager.getAllNotes()) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<Note?>(null) }
    
    val displayedNotes = if (searchQuery.isBlank()) {
        notes
    } else {
        notesManager.searchNotes(searchQuery)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notes (${notes.size})",
                style = MaterialTheme.typography.headlineSmall
            )
            
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search notes...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (displayedNotes.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (searchQuery.isBlank()) "No notes yet!" else "No notes found",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (searchQuery.isBlank()) {
                        Text(
                            text = "Tap the + button to add one",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(displayedNotes) { note ->
                    NoteCard(
                        note = note,
                        onEdit = { editingNote = note },
                        onDelete = {
                            notesManager.deleteNote(note.id)
                            notes = notesManager.getAllNotes()
                        }
                    )
                }
            }
        }
        
        // Add/Edit Dialog
        if (showAddDialog || editingNote != null) {
            var title by remember { mutableStateOf(editingNote?.title ?: "") }
            var content by remember { mutableStateOf(editingNote?.content ?: "") }
            var color by remember { mutableStateOf(editingNote?.color ?: "#FFFFFF") }
            
            AlertDialog(
                onDismissRequest = {
                    showAddDialog = false
                    editingNote = null
                },
                title = { Text(if (editingNote != null) "Edit Note" else "Add New Note") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            label = { Text("Content") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 5
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("#FFB3BA", "#BAFFC9", "#BAE1FF", "#FFFFBA", "#FFDFBA", "#FFFFFF").forEach { c ->
                                val colorValue = parseHexColor(c)
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(colorValue)
                                        .then(
                                            if (color == c) Modifier.border(2.dp, MaterialTheme.colorScheme.primary)
                                            else Modifier
                                        )
                                        .clickable { color = c }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                if (editingNote != null) {
                                    notesManager.updateNote(editingNote!!.id, title, content, color)
                                } else {
                                    notesManager.addNote(title, content, color)
                                }
                                notes = notesManager.getAllNotes()
                                showAddDialog = false
                                editingNote = null
                            }
                        }
                    ) {
                        Text(if (editingNote != null) "Update" else "Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showAddDialog = false
                        editingNote = null
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun NoteCard(
    note: Note,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = parseHexColor(note.color)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (note.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

