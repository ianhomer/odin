// Provider component requires React import even though not explicitly referenced
/* eslint no-unused-vars: "off" */
import React from 'react';
import { render } from 'react-dom';
import { createStore } from 'redux';
import { Provider } from 'react-redux';
import App from './app';
import reducer from './reducers';

const store = createStore(reducer);

render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById('root')
);