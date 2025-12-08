import { useState } from 'react';
import { Calculator as KotlinCalculator } from 'shared';
import './Calculator.css';

export function Calculator() {
  const [calculator] = useState(() => new KotlinCalculator());
  const [display, setDisplay] = useState('0');
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const handleNumber = (num: string) => {
    setErrorMessage(null);
    setDisplay(display === '0' ? num : display + num);
  };

  const handleOperator = (op: string) => {
    setErrorMessage(null);
    setDisplay(display + ` ${op} `);
  };

  const handleClear = () => {
    setDisplay('0');
    setErrorMessage(null);
  };

  const handleEquals = () => {
    try {
      const result = calculator.calculate(display);
      const formatted = result % 1 === 0 ? result.toString() : result.toFixed(2);
      setDisplay(formatted);
      setErrorMessage(null);
    } catch (e: any) {
      setErrorMessage(e.message || 'Error');
    }
  };

  const handleSqrt = () => {
    try {
      const num = parseFloat(display) || 0;
      const result = calculator.sqrt(num);
      const formatted = result % 1 === 0 ? result.toString() : result.toFixed(2);
      setDisplay(formatted);
      setErrorMessage(null);
    } catch (e: any) {
      setErrorMessage(e.message || 'Error');
    }
  };

  return (
    <div className="calculator-container">
      <h2>Calculator</h2>
      <div className="calculator-display">
        <div className="calculator-screen">{display}</div>
        {errorMessage && <div className="calculator-error">{errorMessage}</div>}
      </div>
      <div className="calculator-buttons">
        <div className="calculator-row">
          <button className="calc-btn number" onClick={() => handleNumber('7')}>7</button>
          <button className="calc-btn number" onClick={() => handleNumber('8')}>8</button>
          <button className="calc-btn number" onClick={() => handleNumber('9')}>9</button>
          <button className="calc-btn operator" onClick={handleClear}>C</button>
        </div>
        <div className="calculator-row">
          <button className="calc-btn number" onClick={() => handleNumber('4')}>4</button>
          <button className="calc-btn number" onClick={() => handleNumber('5')}>5</button>
          <button className="calc-btn number" onClick={() => handleNumber('6')}>6</button>
          <button className="calc-btn operator" onClick={() => handleOperator('+')}>+</button>
        </div>
        <div className="calculator-row">
          <button className="calc-btn number" onClick={() => handleNumber('1')}>1</button>
          <button className="calc-btn number" onClick={() => handleNumber('2')}>2</button>
          <button className="calc-btn number" onClick={() => handleNumber('3')}>3</button>
          <button className="calc-btn operator" onClick={() => handleOperator('-')}>-</button>
        </div>
        <div className="calculator-row">
          <button className="calc-btn number wide" onClick={() => handleNumber('0')}>0</button>
          <button className="calc-btn number" onClick={() => handleNumber('.')}>.</button>
          <button className="calc-btn operator" onClick={handleEquals}>=</button>
        </div>
        <div className="calculator-row">
          <button className="calc-btn operator" onClick={() => handleOperator('*')}>×</button>
          <button className="calc-btn operator" onClick={() => handleOperator('/')}>÷</button>
          <button className="calc-btn operator" onClick={() => handleOperator('^')}>^</button>
          <button className="calc-btn operator" onClick={handleSqrt}>√</button>
        </div>
      </div>
      <p className="calculator-info">Example: 5 + 3 or 2 ^ 3</p>
    </div>
  );
}

