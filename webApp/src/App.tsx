import { useState } from 'react';
import { Greeting } from './components/Greeting/Greeting';
import { Counter } from './components/Counter/Counter';
import { Calculator } from './components/Calculator/Calculator';
import { TodoList } from './components/TodoList/TodoList';
import { Timer } from './components/Timer/Timer';
import { Notes } from './components/Notes/Notes';
import './App.css';

type Screen = 'home' | 'counter' | 'calculator' | 'todolist' | 'timer' | 'notes';

export function App() {
  const [currentScreen, setCurrentScreen] = useState<Screen>('home');

  const screens = [
    { id: 'home' as Screen, label: 'Home', icon: '🏠' },
    { id: 'counter' as Screen, label: 'Counter', icon: '🔢' },
    { id: 'calculator' as Screen, label: 'Calculator', icon: '🧮' },
    { id: 'todolist' as Screen, label: 'Todos', icon: '✅' },
    { id: 'timer' as Screen, label: 'Timer', icon: '⏱️' },
    { id: 'notes' as Screen, label: 'Notes', icon: '📝' },
  ];

  return (
    <div className="app">
      <header className="app-header">
        <h1>Compose Multiplatform Demo</h1>
        <p>Shared Kotlin Business Logic • React Web App</p>
      </header>

      <nav className="app-nav">
        {screens.map((screen) => (
          <button
            key={screen.id}
            className={`nav-btn ${currentScreen === screen.id ? 'active' : ''}`}
            onClick={() => setCurrentScreen(screen.id)}
          >
            <span className="nav-icon">{screen.icon}</span>
            <span className="nav-label">{screen.label}</span>
          </button>
        ))}
      </nav>

      <main className="app-main">
        {currentScreen === 'home' && (
          <div className="home-content">
            <Greeting />
            <div className="features-list">
              <h2>Features</h2>
              <ul>
                <li>🔢 Counter - Increment/Decrement demo</li>
                <li>🧮 Calculator - Basic calculations</li>
                <li>✅ Todo List - Task management</li>
                <li>⏱️ Timer/Stopwatch - Time tracking</li>
                <li>📝 Notes - Colorful note taking</li>
                <li>💻 Shared business logic across platforms</li>
              </ul>
            </div>
          </div>
        )}
        {currentScreen === 'counter' && <Counter />}
        {currentScreen === 'calculator' && <Calculator />}
        {currentScreen === 'todolist' && <TodoList />}
        {currentScreen === 'timer' && <Timer />}
        {currentScreen === 'notes' && <Notes />}
      </main>
    </div>
  );
}

