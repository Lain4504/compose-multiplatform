# CRUD API Documentation

## Overview

This project implements a CRUD (Create, Read, Update, Delete) API for tasks using Ktor server with shared business logic across Android (Compose), iOS (SwiftUI), and Web (React) platforms.

## Architecture

### Shared Module (`shared/`)
- **API Layer**: `TaskApi` interface and `TaskApiImpl` - HTTP client wrapper
- **Repository Layer**: `TaskRepository` interface and `TaskRepositoryImpl` - Business logic with StateFlow
- **DTOs**: `TaskDto` - Data transfer objects
- **Configuration**: Platform-specific base URLs via `ApiConfig`

### Server (`server/`)
- **Ktor Server**: RESTful API endpoints
- **In-memory storage**: Tasks stored in memory (replace with database in production)

## API Endpoints

Base URL: `http://localhost:8081` (or `http://10.0.2.2:8081` for Android emulator)

### GET /api/tasks
Get all tasks

**Response:**
```json
{
  "tasks": [
    {
      "id": "uuid",
      "title": "Task title",
      "description": "Task description",
      "isCompleted": false,
      "createdAt": 1234567890
    }
  ]
}
```

### GET /api/tasks/{id}
Get task by ID

**Response:**
```json
{
  "task": {
    "id": "uuid",
    "title": "Task title",
    "description": "Task description",
    "isCompleted": false,
    "createdAt": 1234567890
  }
}
```

### POST /api/tasks
Create new task

**Request Body:**
```json
{
  "title": "Task title",
  "description": "Task description",
  "isCompleted": false
}
```

**Response:**
```json
{
  "task": {
    "id": "uuid",
    "title": "Task title",
    "description": "Task description",
    "isCompleted": false,
    "createdAt": 1234567890
  }
}
```

### PUT /api/tasks/{id}
Update task

**Request Body:**
```json
{
  "title": "Updated title",
  "description": "Updated description",
  "isCompleted": true
}
```

**Response:**
```json
{
  "task": {
    "id": "uuid",
    "title": "Updated title",
    "description": "Updated description",
    "isCompleted": true,
    "createdAt": 1234567890
  }
}
```

### DELETE /api/tasks/{id}
Delete task

**Response:** 204 No Content

## Platform-Specific Configuration

### Android
- Base URL: `http://10.0.2.2:8081` (Android emulator localhost)
- Uses Ktor Client Android engine

### iOS
- Base URL: `http://localhost:8081` (iOS simulator localhost)
- Uses Ktor Client Darwin engine

### Web
- Base URL: `http://localhost:8081`
- Uses Ktor Client JS engine
- Web app runs on port `3000`

## Usage Examples

### Android (Compose)
```kotlin
val httpClient = createHttpClient()
val taskApi = TaskApiImpl(ApiConfig.baseUrl, httpClient)
val repository = TaskRepositoryImpl(taskApi)

// Observe tasks
val tasks by repository.tasks.collectAsState()

// Load tasks
LaunchedEffect(Unit) {
    repository.loadTasks()
}

// Create task
scope.launch {
    repository.createTask("Title", "Description")
}
```

### iOS (SwiftUI)
```swift
let httpClient = createHttpClient()
let taskApi = TaskApiImpl(baseUrl: ApiConfig.shared.baseUrl, httpClient: httpClient)
let repository = TaskRepositoryImpl(taskApi: taskApi)

// Use repository methods with async/await
Task {
    await repository.loadTasks()
}
```

### Web (React/TypeScript)
```typescript
import { TaskRepositoryHelper, TaskApiImpl, ApiConfig, createHttpClient } from 'shared';

const httpClient = createHttpClient();
const taskApi = new TaskApiImpl(ApiConfig.baseUrl, httpClient);
const repository = new TaskRepositoryImpl(taskApi);
const helper = new TaskRepositoryHelper(repository);

// Use helper methods
await helper.loadTasks();
```

## Running the Server

```bash
./gradlew :server:run
```

Server will start on `http://localhost:8081`
Web app will start on `http://localhost:3000`

## Best Practices

1. **Repository Pattern**: Business logic centralized in shared module
2. **StateFlow**: Reactive state management with Kotlin Flow
3. **Error Handling**: Result types and error states
4. **Platform Abstraction**: Platform-specific HTTP clients via expect/actual
5. **Type Safety**: Shared DTOs ensure consistency across platforms

## Future Improvements

- [ ] Add database persistence (SQLite/PostgreSQL)
- [ ] Add authentication/authorization
- [ ] Add pagination for large lists
- [ ] Add filtering and sorting
- [ ] Add unit tests
- [ ] Add integration tests

