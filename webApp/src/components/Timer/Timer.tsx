import { useState, useEffect } from 'react';
import { Timer as KotlinTimer } from 'shared';
import './Timer.css';

export function Timer() {
  const [timer] = useState(() => new KotlinTimer());
  const [formattedTime, setFormattedTime] = useState(timer.formatTime());
  const [status, setStatus] = useState(timer.getStatus());
  const [isRunning, setIsRunning] = useState(false);

  useEffect(() => {
    let interval: NodeJS.Timeout | null = null;
    if (isRunning) {
      interval = setInterval(() => {
        setFormattedTime(timer.formatTime());
        setStatus(timer.getStatus());
      }, 100);
    }
    return () => {
      if (interval) clearInterval(interval);
    };
  }, [isRunning, timer]);

  const handleStartPause = () => {
    if (isRunning) {
      timer.pause();
      setIsRunning(false);
    } else {
      timer.start();
      setIsRunning(true);
    }
    setFormattedTime(timer.formatTime());
    setStatus(timer.getStatus());
  };

  const handleReset = () => {
    timer.reset();
    setIsRunning(false);
    setFormattedTime(timer.formatTime());
    setStatus(timer.getStatus());
  };

  return (
    <div className="timer-container">
      <h2>Stopwatch</h2>
      <div className="timer-display">{formattedTime}</div>
      <div className="timer-status">Status: {status}</div>
      <div className="timer-buttons">
        <button
          className={`timer-btn ${isRunning ? 'pause' : 'start'}`}
          onClick={handleStartPause}
        >
          {isRunning ? '⏸ Pause' : '▶ Start'}
        </button>
        <button className="timer-btn reset" onClick={handleReset}>
          ⟲ Reset
        </button>
      </div>
      <p className="timer-info">This timer uses shared Kotlin business logic</p>
    </div>
  );
}

