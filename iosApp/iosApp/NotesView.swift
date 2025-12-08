import SwiftUI
import Shared

struct NotesView: View {
    @StateObject private var viewModel = NotesViewModel()
    @State private var showAddDialog = false
    @State private var editingNote: Note? = nil
    
    var body: some View {
        VStack(spacing: 0) {
            // Header
            HStack {
                Text("Notes (\(viewModel.notes.count))")
                    .font(.title3)
                    .fontWeight(.semibold)
                Spacer()
                Button(action: { showAddDialog = true }) {
                    Image(systemName: "plus.circle.fill")
                        .font(.title2)
                        .foregroundColor(.blue)
                }
            }
            .padding()
            
            // Search Bar
            HStack {
                Image(systemName: "magnifyingglass")
                    .foregroundColor(.gray)
                TextField("Search notes...", text: $viewModel.searchQuery)
            }
            .padding()
            .background(Color(.systemGray6))
            .cornerRadius(10)
            .padding(.horizontal)
            
            // Notes Grid
            if viewModel.displayedNotes.isEmpty {
                VStack(spacing: 12) {
                    Image(systemName: "note.text")
                        .font(.system(size: 60))
                        .foregroundColor(.gray)
                    Text(viewModel.searchQuery.isEmpty ? "No notes yet!" : "No notes found")
                        .font(.headline)
                    if viewModel.searchQuery.isEmpty {
                        Text("Tap the + button to add one")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else {
                ScrollView {
                    LazyVGrid(columns: [
                        GridItem(.flexible(), spacing: 12),
                        GridItem(.flexible(), spacing: 12)
                    ], spacing: 12) {
                        ForEach(viewModel.displayedNotes, id: \.id) { note in
                            NoteCard(note: note) {
                                editingNote = note
                                showAddDialog = true
                            } onDelete: {
                                viewModel.deleteNote(id: note.id)
                            }
                        }
                    }
                    .padding()
                }
            }
        }
        .navigationTitle("Notes")
        .sheet(isPresented: $showAddDialog) {
            NoteDialog(
                note: editingNote,
                onSave: { title, content, color in
                    if let editingNote = editingNote {
                        viewModel.updateNote(id: editingNote.id, title: title, content: content, color: color)
                    } else {
                        viewModel.addNote(title: title, content: content, color: color)
                    }
                    showAddDialog = false
                    editingNote = nil
                },
                onDismiss: {
                    showAddDialog = false
                    editingNote = nil
                }
            )
        }
    }
}

struct NoteCard: View {
    let note: Note
    let onEdit: () -> Void
    let onDelete: () -> Void
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(note.title)
                .font(.headline)
                .lineLimit(2)
            
            if !note.content.isEmpty {
                Text(note.content)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .lineLimit(3)
            }
            
            Spacer()
            
            HStack {
                Spacer()
                Button(action: onEdit) {
                    Image(systemName: "pencil")
                        .font(.caption)
                        .foregroundColor(.blue)
                }
                Button(action: onDelete) {
                    Image(systemName: "trash")
                        .font(.caption)
                        .foregroundColor(.red)
                }
            }
        }
        .padding()
        .frame(height: 120)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(hexColor(note.color))
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.1), radius: 3, x: 0, y: 2)
    }
    
    private func hexColor(_ hex: String) -> Color {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 6: // RGB (24-bit)
            (r, g, b) = ((int >> 16) & 0xFF, (int >> 8) & 0xFF, int & 0xFF)
            return Color(red: Double(r) / 255, green: Double(g) / 255, blue: Double(b) / 255)
        default:
            return Color.white
        }
    }
}

struct NoteDialog: View {
    let note: Note?
    let onSave: (String, String, String) -> Void
    let onDismiss: () -> Void
    
    @State private var title: String = ""
    @State private var content: String = ""
    @State private var selectedColor: String = "#FFFFFF"
    
    @Environment(\.dismiss) private var dismiss
    
    let colors = ["#FFB3BA", "#BAFFC9", "#BAE1FF", "#FFFFBA", "#FFDFBA", "#FFFFFF"]
    
    init(note: Note?, onSave: @escaping (String, String, String) -> Void, onDismiss: @escaping () -> Void) {
        self.note = note
        self.onSave = onSave
        self.onDismiss = onDismiss
        _title = State(initialValue: note?.title ?? "")
        _content = State(initialValue: note?.content ?? "")
        _selectedColor = State(initialValue: note?.color ?? "#FFFFFF")
    }
    
    var body: some View {
        NavigationStack {
            Form {
                Section("Note Details") {
                    TextField("Title", text: $title)
                    TextField("Content", text: $content, axis: .vertical)
                        .lineLimit(5...10)
                }
                
                Section("Color") {
                    HStack {
                        ForEach(colors, id: \.self) { color in
                            Button(action: {
                                selectedColor = color
                            }) {
                                Circle()
                                    .fill(hexColor(color))
                                    .frame(width: 40, height: 40)
                                    .overlay(
                                        Circle()
                                            .stroke(selectedColor == color ? Color.blue : Color.clear, lineWidth: 3)
                                    )
                            }
                        }
                    }
                }
            }
            .navigationTitle(note != nil ? "Edit Note" : "Add Note")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        onDismiss()
                    }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button(note != nil ? "Update" : "Add") {
                        if !title.isEmpty {
                            onSave(title, content, selectedColor)
                        }
                    }
                    .disabled(title.isEmpty)
                }
            }
        }
    }
    
    private func hexColor(_ hex: String) -> Color {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let r, g, b: UInt64
        switch hex.count {
        case 6:
            (r, g, b) = ((int >> 16) & 0xFF, (int >> 8) & 0xFF, int & 0xFF)
            return Color(red: Double(r) / 255, green: Double(g) / 255, blue: Double(b) / 255)
        default:
            return Color.white
        }
    }
}

class NotesViewModel: ObservableObject {
    private let notesManager = NotesManager()
    @Published var notes: [Note] = []
    @Published var searchQuery: String = ""
    
    var displayedNotes: [Note] {
        if searchQuery.isEmpty {
            return notes
        } else {
            return notesManager.searchNotes(query: searchQuery)
        }
    }
    
    init() {
        refreshNotes()
    }
    
    func refreshNotes() {
        notes = notesManager.getAllNotes()
    }
    
    func addNote(title: String, content: String, color: String) {
        notesManager.addNote(title: title, content: content, color: color)
        refreshNotes()
    }
    
    func updateNote(id: String, title: String, content: String, color: String) {
        _ = notesManager.updateNote(id: id, title: title, content: content, color: color)
        refreshNotes()
    }
    
    func deleteNote(id: String) {
        notesManager.deleteNote(id: id)
        refreshNotes()
    }
}

#Preview {
    NavigationStack {
        NotesView()
    }
}

