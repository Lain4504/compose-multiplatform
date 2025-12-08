import { useState } from 'react';
import { Counter as KotlinCounter } from 'shared';
import './Counter.css';

export function Counter() {
  const [counter] = useState(() => new KotlinCounter());
  const [count, setCount] = useState(counter.getValue());

  const handleIncrement = () => {
    setCount(counter.increment());
  };

  const handleDecrement = () => {
    setCount(counter.decrement());
  };

  const handleReset = () => {
    setCount(counter.reset());
  };

  return (
    <div className="counter-container">
      <h2>Counter Demo</h2>
      <div className="counter-display">{count}</div>
      <div className="counter-buttons">
        <button onClick={handleDecrement} className="counter-btn decrement">
          - Decrement
        </button>
        <button onClick={handleReset} className="counter-btn reset">
          Reset
        </button>
        <button onClick={handleIncrement} className="counter-btn increment">
          + Increment
        </button>
      </div>
      <p className="counter-info">This counter uses shared Kotlin business logic</p>
    </div>
  );
}

