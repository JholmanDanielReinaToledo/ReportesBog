import React, { useState } from 'react';
import logo from './logo.svg';
import './App.css';
import { Input } from 'antd';
import { map } from 'lodash';

const App = () => {
  const [list, setList] = useState<string[]>(['asdasd', 'fdadf']);

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
      {
        map(
          list,
          (item) => <h4>{item}</h4>
        )
      }
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
