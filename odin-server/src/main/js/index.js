import React from 'react'
import { render } from 'react-dom'

import thunkMiddleware from 'redux-thunk'
import { createLogger } from 'redux-logger'
import { createStore, applyMiddleware } from 'redux'
import { Provider } from 'react-redux'

import App from './app'
import reducer from './reducers'
import { Flux } from './flux/flux'

const store = createStore(
  reducer,
  applyMiddleware(
    thunkMiddleware,
    createLogger()
  )
)

render(
  <Provider store={store}>
    <App flux={new Flux()} dispatch={store.dispatch}/>
  </Provider>,
  document.getElementById('root')
)