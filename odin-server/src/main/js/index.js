import React from 'react'
import { render } from 'react-dom'

import thunkMiddleware from 'redux-thunk'
import { createLogger } from 'redux-logger'
import { createStore, applyMiddleware } from 'redux'

import App from './components/app'
import reducer from './reducers'
import { Flux } from './legacy/flux'

const store = createStore(
  reducer,
  applyMiddleware(
    thunkMiddleware,
    createLogger()
  )
)

render(
  <App store={store} flux={new Flux()}/>,
  document.getElementById('root')
)