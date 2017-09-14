import React from 'react'
import { render } from 'react-dom'

import thunkMiddleware from 'redux-thunk'
import { createLogger } from 'redux-logger'
import { createStore, applyMiddleware } from 'redux'
import { Provider } from 'react-redux'

import App from './containers/app'
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
    <App flux={new Flux()}/>
  </Provider>,
  document.getElementById('root')
)