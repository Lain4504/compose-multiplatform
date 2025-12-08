import SwiftUI
import Shared

struct TodoListView: View {
    @StateObject private var viewModel = TodoListViewModel()
    @State private var showAddDialog = false
    
    var body: some View {
        VStack(spacing: 0) {
            // Header
            HStack {
                Text("Todo List (\(viewModel.todos.count) items)")
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
            
            // List
            if viewModel.todos.isEmpty {
                VStack(spacing: 12) {
                    Image(systemName: "checklist")
                        .font(.system(size: 60))
                        .foregroundColor(.gray)
                    Text("No todos yet!")
                        .font(.headline)
                    Text("Tap the + button to add one")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else {
                List {
                    ForEach(viewModel.todos, id: \.id) { todo in
                        TodoRow(todo: todo) {
                            viewModel.toggleTodo(id: todo.id)
                        } onDelete: {
                            viewModel.deleteTodo(id: todo.id)
                        }
                    }
                }
                .listStyle(.plain)
            }
        }
        .navigationTitle("Todos")
        .sheet(isPresented: $showAddDialog) {
            AddTodoDialog { title, description in
                viewModel.addTodo(title: title, description: description)
                showAddDialog = false
            }
        }
    }
}

struct TodoRow: View {
    let todo: TodoItem
    let onToggle: () -> Void
    let onDelete: () -> Void
    
    var body: some View {
        HStack(spacing: 12) {
            Button(action: onToggle) {
                Image(systemName: todo.isCompleted ? "checkmark.circle.fill" : "circle")
                    .foregroundColor(todo.isCompleted ? .green : .gray)
                    .font(.title3)
            }
            
            VStack(alignment: .leading, spacing: 4) {
                Text(todo.title)
                    .font(.body)
                    .strikethrough(todo.isCompleted)
                    .foregroundColor(todo.isCompleted ? .secondary : .primary)
                
                if !todo.description.isEmpty {
                    Text(todo.description)
                        .font(.caption)
                        .foregroundColor(.secondary)
                        .lineLimit(2)
                }
            }
            
            Spacer()
            
            Button(action: onDelete) {
                Image(systemName: "trash")
                    .foregroundColor(.red)
            }
        }
        .padding(.vertical, 4)
    }
}

struct AddTodoDialog: View {
    @State private var title = ""
    @State private var description = ""
    let onSave: (String, String) -> Void
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        NavigationStack {
            Form {
                Section("Todo Details") {
                    TextField("Title", text: $title)
                    TextField("Description (optional)", text: $description, axis: .vertical)
                        .lineLimit(3...6)
                }
            }
            .navigationTitle("Add Todo")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        dismiss()
                    }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Add") {
                        if !title.isEmpty {
                            onSave(title, description)
                        }
                    }
                    .disabled(title.isEmpty)
                }
            }
        }
    }
}

class TodoListViewModel: ObservableObject {
    private let todoManager = TodoManager()
    @Published var todos: [TodoItem] = []
    
    init() {
        refreshTodos()
    }
    
    func refreshTodos() {
        todos = todoManager.getAllTodos()
    }
    
    func addTodo(title: String, description: String) {
        todoManager.addTodo(title: title, description: description)
        refreshTodos()
    }
    
    func toggleTodo(id: String) {
        todoManager.toggleTodo(id: id)
        refreshTodos()
    }
    
    func deleteTodo(id: String) {
        todoManager.removeTodo(id: id)
        refreshTodos()
    }
}

#Preview {
    NavigationStack {
        TodoListView()
    }
}

