import React from 'react'
import { render } from 'react-dom'

import thunkMiddleware from 'redux-thunk'
import { createLogger } from 'redux-logger'
import { createStore, applyMiddleware } from 'redux'
import 'babel-polyfill'
import createSagaMiddleware from 'redux-saga'

import App from './components/app'
import reducer from './reducers'
import { Flux } from './legacy/flux'
import backend from './backend'

const sagaMiddleware = createSagaMiddleware()
const store = createStore(
  reducer,
  applyMiddleware(
    thunkMiddleware,
    sagaMiddleware,
    createLogger()
  )
)

sagaMiddleware.run(backend)

render(
  <App store={store} flux={new Flux()}/>,
  document.getElementById('root')
)