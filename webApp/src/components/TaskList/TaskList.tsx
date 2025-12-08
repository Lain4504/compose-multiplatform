import { useState, useEffect } from 'react';
import { TaskRepositoryHelper, TaskApiImpl, ApiConfig, createHttpClient } from 'shared';
import './TaskList.css';

let repositoryHelper: TaskRepositoryHelper | null = null;

function getRepository(): TaskRepositoryHelper {
  if (!repositoryHelper) {
    const httpClient = createHttpClient();
    const taskApi = new TaskApiImpl(ApiConfig.baseUrl, httpClient);
    const repository = new TaskRepositoryImpl(taskApi);
    repositoryHelper = new TaskRepositoryHelper(repository);
  }
  return repositoryHelper;
}

export function TaskList() {
  const [tasks, setTasks] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showAddDialog, setShowAddDialog] = useState(false);

  const repository = getRepository();

  useEffect(() => {
    loadTasks();
    
    // Subscribe to tasks updates
    const subscription = repository.getTasksFlow().subscribe((newTasks: any[]) => {
      setTasks(newTasks);
    });
    
    // Subscribe to loading state
    const loadingSubscription = repository.getLoadingFlow().subscribe((loading: boolean) => {
      setIsLoading(loading);
    });
    
    // Subscribe to errors
    const errorSubscription = repository.getErrorFlow().subscribe((err: string | null) => {
      setError(err);
    });
    
    return () => {
      subscription.cancel();
      loadingSubscription.cancel();
      errorSubscription.cancel();
    };
  }, []);

  const loadTasks = async () => {
    try {
      await repository.loadTasks();
    } catch (e: any) {
      setError(e.message || 'Failed to load tasks');
    }
  };

  const handleAdd = async (title: string, description: string) => {
    try {
      await repository.createTask(title, description);
      setShowAddDialog(false);
    } catch (e: any) {
      setError(e.message || 'Failed to create task');
    }
  };

  const handleToggle = async (id: string) => {
    try {
      await repository.toggleTask(id);
    } catch (e: any) {
      setError(e.message || 'Failed to toggle task');
    }
  };

  const handleDelete = async (id: string) => {
    try {
      await repository.deleteTask(id);
    } catch (e: any) {
      setError(e.message || 'Failed to delete task');
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
                  onChange={() => handleToggle(task.id)}
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
                onClick={() => handleDelete(task.id)}
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
        />
        <textarea
          placeholder="Description (optional)"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          className="dialog-textarea"
          rows={3}
        />
        <div className="dialog-buttons">
          <button className="dialog-btn cancel" onClick={onClose}>
            Cancel
          </button>
          <button
            className="dialog-btn confirm"
            onClick={() => {
              if (title.trim()) {
                onSave(title, description);
              }
            }}
          >
            Add
          </button>
        </div>
      </div>
    </div>
  );
}

