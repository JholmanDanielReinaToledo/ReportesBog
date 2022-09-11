import React, { useState } from 'react';
import logo from './logo.svg';
import './App.css';
import { Input } from 'antd';

const App = () => {
  const [list, setList] = useState<string[]>();

  const addItemList = (item: string) => {
    if (item) {
      setList([item, ...list])
    }
  }

  return (
    <div className="App">
      <Input
        name='item'
      />
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.tsx</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
  );
}

export default App;
