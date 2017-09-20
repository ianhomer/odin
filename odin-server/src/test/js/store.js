import { applyMiddleware, combineReducers, createStore } from 'redux'
import createSagaMiddleware from 'redux-saga'
import thunkMiddleware from 'redux-thunk'

import { MockBackend } from './backend/mock.js'
import reducers from 'odin/reducers/index.js'
import { createLogger } from 'redux-logger'

function lastAction(state = null, action) {  // eslint-disable-line no-unused-vars
  return action
}

reducers.lastAction = lastAction
const combinedReducers = combineReducers(reducers)

const backend = new MockBackend()
const sagaMiddleware = createSagaMiddleware({context: {backend : backend}})
const store = createStore(
  combinedReducers,
  applyMiddleware(
    thunkMiddleware,
    sagaMiddleware,
    createLogger({collapsed : false, timestamp : true, colors : {action : false}})
  )
)
sagaMiddleware.run(backend.saga)

export default store