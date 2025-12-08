import SwiftUI
import Shared

struct TaskListView: View {
    @StateObject private var viewModel = TaskListViewModel()
    @State private var showAddDialog = false
    
    var body: some View {
        VStack(spacing: 0) {
            // Header
            HStack {
                Text("Tasks (\(viewModel.tasks.count))")
                    .font(.title3)
                    .fontWeight(.semibold)
                Spacer()
                HStack(spacing: 8) {
                    Button(action: {
                        Task {
                            await viewModel.loadTasks()
                        }
                    }) {
                        Image(systemName: "arrow.clockwise")
                            .foregroundColor(.blue)
                    }
                    .disabled(viewModel.isLoading)
                    
                    Button(action: { showAddDialog = true }) {
                        Image(systemName: "plus.circle.fill")
                            .font(.title2)
                            .foregroundColor(.blue)
                    }
                }
            }
            .padding()
            
            // Error Message
            if let error = viewModel.error {
                HStack {
                    Image(systemName: "exclamationmark.triangle.fill")
                        .foregroundColor(.red)
                    Text(error)
                        .font(.caption)
                        .foregroundColor(.red)
                }
                .padding()
                .frame(maxWidth: .infinity)
                .background(Color.red.opacity(0.1))
            }
            
            // List
            if viewModel.isLoading && viewModel.tasks.isEmpty {
                Spacer()
                ProgressView()
                Spacer()
            } else if viewModel.tasks.isEmpty {
                VStack(spacing: 12) {
                    Image(systemName: "checklist")
                        .font(.system(size: 60))
                        .foregroundColor(.gray)
                    Text("No tasks yet!")
                        .font(.headline)
                    Text("Tap the + button to add one")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else {
                List {
                    ForEach(viewModel.tasks, id: \.id) { task in
                        TaskRow(task: task) {
                            Task {
                                await viewModel.toggleTask(id: task.id ?? "")
                            }
                        } onDelete: {
                            Task {
                                await viewModel.deleteTask(id: task.id ?? "")
                            }
                        }
                    }
                }
                .listStyle(.plain)
                .refreshable {
                    await viewModel.loadTasks()
                }
            }
        }
        .navigationTitle("Tasks")
        .sheet(isPresented: $showAddDialog) {
            AddTaskDialog { title, description in
                Task {
                    await viewModel.createTask(title: title, description: description)
                    showAddDialog = false
                }
            }
        }
        .task {
            await viewModel.loadTasks()
        }
    }
}

struct TaskRow: View {
    let task: TaskDto
    let onToggle: () -> Void
    let onDelete: () -> Void
    
    var body: some View {
        HStack(spacing: 12) {
            Button(action: onToggle) {
                Image(systemName: task.isCompleted ? "checkmark.circle.fill" : "circle")
                    .foregroundColor(task.isCompleted ? .green : .gray)
                    .font(.title3)
            }
            
            VStack(alignment: .leading, spacing: 4) {
                Text(task.title)
                    .font(.body)
                    .strikethrough(task.isCompleted)
                    .foregroundColor(task.isCompleted ? .secondary : .primary)
                
                if !task.description.isEmpty {
                    Text(task.description)
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

struct AddTaskDialog: View {
    @State private var title = ""
    @State private var description = ""
    let onSave: (String, String) -> Void
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        NavigationStack {
            Form {
                Section("Task Details") {
                    TextField("Title", text: $title)
                    TextField("Description (optional)", text: $description, axis: .vertical)
                        .lineLimit(3...6)
                }
            }
            .navigationTitle("Add Task")
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

@MainActor
class TaskListViewModel: ObservableObject {
    private var httpClient: HttpClient?
    private var taskApi: TaskApi?
    private var repository: TaskRepository?
    
    @Published var tasks: [TaskDto] = []
    @Published var isLoading: Bool = false
    @Published var error: String? = nil
    
    init() {
        Task {
            await initializeRepository()
        }
    }
    
    private func initializeRepository() async {
        do {
            httpClient = createHttpClient()
            taskApi = TaskApiImpl(baseUrl: ApiConfig.shared.baseUrl, httpClient: httpClient!)
            repository = TaskRepositoryImpl(taskApi: taskApi!)
            
            // Observe tasks
            Task {
                for await taskList in repository!.tasks {
                    await MainActor.run {
                        self.tasks = taskList
                    }
                }
            }
            
            // Observe loading state
            Task {
                for await loading in repository!.isLoading {
                    await MainActor.run {
                        self.isLoading = loading
                    }
                }
            }
            
            // Observe errors
            Task {
                for await errorMsg in repository!.error {
                    await MainActor.run {
                        self.error = errorMsg
                    }
                }
            }
        } catch {
            error = "Failed to initialize: \(error.localizedDescription)"
        }
    }
    
    func loadTasks() async {
        await repository?.loadTasks()
    }
    
    func createTask(title: String, description: String) async {
        let result = await repository?.createTask(title: title, description: description)
        if case .failure(let err) = result {
            error = err.localizedDescription
        }
    }
    
    func toggleTask(id: String) async {
        let result = await repository?.toggleTask(id: id)
        if case .failure(let err) = result {
            error = err.localizedDescription
        }
    }
    
    func deleteTask(id: String) async {
        let result = await repository?.deleteTask(id: id)
        if case .failure(let err) = result {
            error = err.localizedDescription
        }
    }
}

#Preview {
    NavigationStack {
        TaskListView()
    }
}

