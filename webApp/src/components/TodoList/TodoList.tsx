import { useState } from 'react';
import { TodoManager, TodoItem } from 'shared';
import './TodoList.css';

export function TodoList() {
  const [todoManager] = useState(() => new TodoManager());
  const [todos, setTodos] = useState<TodoItem[]>(() => {
    const result = todoManager.getAllTodos();
    // Convert Kotlin List to JavaScript Array
    return Array.isArray(result) ? result : Array.from(result as any);
  });
  const [newTodoTitle, setNewTodoTitle] = useState('');
  const [newTodoDescription, setNewTodoDescription] = useState('');
  const [showAddDialog, setShowAddDialog] = useState(false);

  const refreshTodos = () => {
    try {
      const result = todoManager.getAllTodos();
      // Convert Kotlin List to JavaScript Array
      const todosArray = Array.isArray(result) ? result : Array.from(result as any);
      console.log('Refreshing todos, count:', todosArray.length);
      setTodos(todosArray);
    } catch (error) {
      console.error('Error refreshing todos:', error);
    }
  };

  const handleAdd = () => {
    if (newTodoTitle.trim()) {
      try {
        const added = todoManager.addTodo(newTodoTitle, newTodoDescription);
        console.log('Added todo:', added);
        if (added) {
          // Add the todo to state directly
          setTodos(prevTodos => [...prevTodos, added]);
        } else {
          refreshTodos();
        }
        setNewTodoTitle('');
        setNewTodoDescription('');
        setShowAddDialog(false);
      } catch (error) {
        console.error('Error adding todo:', error);
        // Still refresh even on error to show current state
        refreshTodos();
      }
    }
  };

  const handleToggle = (id: string) => {
    todoManager.toggleTodo(id);
    refreshTodos();
  };

  const handleDelete = (id: string) => {
    todoManager.removeTodo(id);
    refreshTodos();
  };

  return (
    <div className="todolist-container">
      <div className="todolist-header">
        <h2>Todo List ({todos.length} items)</h2>
        <button className="add-btn" onClick={() => setShowAddDialog(true)}>+ Add</button>
      </div>

      {todos.length === 0 ? (
        <div className="todolist-empty">
          <p>No todos yet!</p>
          <p>Click the + button to add one</p>
        </div>
      ) : (
        <div className="todolist-items">
          {todos.map((todo) => (
            <div key={todo.id} className={`todo-item ${todo.isCompleted ? 'completed' : ''}`}>
              <div className="todo-content">
                <input
                  type="checkbox"
                  checked={todo.isCompleted}
                  onChange={() => handleToggle(todo.id)}
                  className="todo-checkbox"
                />
                <div className="todo-text">
                  <div className={`todo-title ${todo.isCompleted ? 'strikethrough' : ''}`}>
                    {todo.title}
                  </div>
                  {todo.description && (
                    <div className="todo-description">{todo.description}</div>
                  )}
                </div>
              </div>
              <button
                className="todo-delete"
                onClick={() => handleDelete(todo.id)}
                aria-label="Delete todo"
              >
                ×
              </button>
            </div>
          ))}
        </div>
      )}

      {showAddDialog && (
        <div className="dialog-overlay" onClick={() => setShowAddDialog(false)}>
          <div className="dialog-content" onClick={(e) => e.stopPropagation()}>
            <h3>Add New Todo</h3>
            <input
              type="text"
              placeholder="Title"
              value={newTodoTitle}
              onChange={(e) => setNewTodoTitle(e.target.value)}
              className="dialog-input"
              autoFocus
            />
            <textarea
              placeholder="Description (optional)"
              value={newTodoDescription}
              onChange={(e) => setNewTodoDescription(e.target.value)}
              className="dialog-textarea"
              rows={3}
            />
            <div className="dialog-buttons">
              <button className="dialog-btn cancel" onClick={() => setShowAddDialog(false)}>
                Cancel
              </button>
              <button className="dialog-btn confirm" onClick={handleAdd}>
                Add
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

