import React from 'react'
import { render } from 'react-dom'
import { createStore } from 'redux'
import { Provider } from 'react-redux'
import App from './app'
import reducer from './reducers'
import { Flux } from './flux/flux'

const store = createStore(reducer)

render(
  <Provider store={store}>
    <App flux={new Flux()}/>
  </Provider>,
  document.getElementById('root')
)