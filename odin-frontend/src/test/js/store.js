import {applyMiddleware, combineReducers, createStore} from 'redux'
import createSagaMiddleware from 'redux-saga'
import thunkMiddleware from 'redux-thunk'

import {MockBackend} from './backend/mock.js'
import reducers from 'odin/reducers/index.js'
import {createLogger} from 'redux-logger'

// Store configuration

const ENABLE_LOGGING = false

// Save last action in store so that dispatch and expect functionality can wait
// the appropriate action before applying the assertion.
function lastAction(state = null, action) {  // eslint-disable-line no-unused-vars
  return action
}

reducers.lastAction = lastAction

const backend = new MockBackend()
const sagaMiddleware = createSagaMiddleware({context: {backend: backend}})

var middleware = [
  thunkMiddleware,
  sagaMiddleware
]
if (ENABLE_LOGGING) {
  middleware.push(
    createLogger({
      collapsed: true,
      colors: {
        action: false
      },
      duration: true,
      level: 'error',
      stateTransformer: state => JSON.stringify(state, null, '  '),
      timestamp: true
    })
  )
}

const store = createStore(
  combineReducers(reducers),
  applyMiddleware(...middleware)
)
sagaMiddleware.run(backend.saga)

export default store