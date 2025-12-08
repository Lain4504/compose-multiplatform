import { useState, useEffect } from 'react';
import { createTaskApiJs, getApiBaseUrl, createHttpClient, TaskDto } from 'shared';
import './TaskList.css';

let taskApiJs: any = null;

function getTaskApi() {
  if (!taskApiJs) {
    const httpClient = createHttpClient();
    const baseUrl = getApiBaseUrl();
    taskApiJs = createTaskApiJs(baseUrl, httpClient);
  }
  return taskApiJs;
}

export function TaskList() {
  const [tasks, setTasks] = useState<TaskDto[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showAddDialog, setShowAddDialog] = useState(false);

  const taskApi = getTaskApi();

  useEffect(() => {
    loadTasks();
  }, []);

  const loadTasks = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const result = await taskApi.getAllTasks();
      setTasks(Array.from(result));
    } catch (e: any) {
      setError(e.message || 'Failed to load tasks');
    } finally {
      setIsLoading(false);
    }
  };

  const handleAdd = async (title: string, description: string) => {
    console.log('handleAdd called with:', { title, description });
    setIsLoading(true);
    setError(null);
    try {
      // Create task with only required fields (id and createdAt will be set by server)
      const newTask: TaskDto = {
        title: title.trim(),
        description: description.trim(),
        isCompleted: false,
      };
      console.log('Creating task:', JSON.stringify(newTask));
      console.log('TaskApi instance:', taskApi);
      const result = await taskApi.createTask(newTask);
      console.log('Task created successfully:', result);
      setShowAddDialog(false);
      await loadTasks(); // Reload tasks
    } catch (e: any) {
      console.error('Failed to create task:', e);
      console.error('Error details:', {
        message: e.message,
        stack: e.stack,
        name: e.name,
      });
      setError(e.message || 'Failed to create task');
    } finally {
      setIsLoading(false);
    }
  };

  const handleToggle = async (id: string) => {
    setIsLoading(true);
    setError(null);
    try {
      const task = tasks.find((t) => t.id === id);
      if (task) {
        const updatedTask = {
          id: task.id,
          title: task.title,
          description: task.description,
          isCompleted: !task.isCompleted,
          createdAt: task.createdAt,
        } as TaskDto;
        await taskApi.updateTask(id, updatedTask);
        await loadTasks(); // Reload tasks
      }
    } catch (e: any) {
      setError(e.message || 'Failed to toggle task');
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async (id: string) => {
    setIsLoading(true);
    setError(null);
    try {
      await taskApi.deleteTask(id);
      await loadTasks(); // Reload tasks
    } catch (e: any) {
      setError(e.message || 'Failed to delete task');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="tasklist-container">
      <div className="tasklist-header">
        <h2>Tasks ({tasks.length})</h2>
        <div className="tasklist-actions">
          <button
            className="refresh-btn"
            onClick={loadTasks}
            disabled={isLoading}
          >
            🔄 Refresh
          </button>
          <button className="add-btn" onClick={() => setShowAddDialog(true)}>
            + Add
          </button>
        </div>
      </div>

      {error && (
        <div className="tasklist-error">
          <span>⚠️ {error}</span>
        </div>
      )}

      {isLoading && tasks.length === 0 ? (
        <div className="tasklist-loading">
          <div className="spinner"></div>
          <p>Loading tasks...</p>
        </div>
      ) : tasks.length === 0 ? (
        <div className="tasklist-empty">
          <p>No tasks yet!</p>
          <p>Click the + button to add one</p>
        </div>
      ) : (
        <div className="tasklist-items">
          {tasks.map((task) => (
            <div
              key={task.id}
              className={`task-item ${task.isCompleted ? 'completed' : ''}`}
            >
              <div className="task-content">
                <input
                  type="checkbox"
                  checked={task.isCompleted}
                  onChange={() => handleToggle(task.id || '')}
                  className="task-checkbox"
                />
                <div className="task-text">
                  <div
                    className={`task-title ${
                      task.isCompleted ? 'strikethrough' : ''
                    }`}
                  >
                    {task.title}
                  </div>
                  {task.description && (
                    <div className="task-description">{task.description}</div>
                  )}
                </div>
              </div>
              <button
                className="task-delete"
                onClick={() => handleDelete(task.id || '')}
                aria-label="Delete task"
              >
                ×
              </button>
            </div>
          ))}
        </div>
      )}

      {showAddDialog && (
        <TaskDialog
          onClose={() => setShowAddDialog(false)}
          onSave={handleAdd}
        />
      )}
    </div>
  );
}

interface TaskDialogProps {
  onClose: () => void;
  onSave: (title: string, description: string) => void;
}

function TaskDialog({ onClose, onSave }: TaskDialogProps) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');

  const handleSave = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    console.log('Save button clicked, title:', title);
    if (title.trim()) {
      console.log('Calling onSave');
      onSave(title.trim(), description);
    } else {
      console.log('Title is empty, not saving');
    }
  };

  const handleCancel = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    onClose();
  };

  return (
    <div className="dialog-overlay" onClick={onClose}>
      <div className="dialog-content" onClick={(e) => e.stopPropagation()}>
        <h3>Add New Task</h3>
        <input
          type="text"
          placeholder="Title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          className="dialog-input"
          autoFocus
          onKeyDown={(e) => {
            if (e.key === 'Enter' && title.trim()) {
              e.preventDefault();
              onSave(title.trim(), description);
            }
          }}
        />
        <textarea
          placeholder="Description (optional)"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          className="dialog-textarea"
          rows={3}
        />
        <div className="dialog-buttons">
          <button 
            type="button"
            className="dialog-btn cancel" 
            onClick={handleCancel}
          >
            Cancel
          </button>
          <button
            type="button"
            className="dialog-btn confirm"
            onClick={handleSave}
            disabled={!title.trim()}
          >
            Add
          </button>
        </div>
      </div>
    </div>
  );
}
