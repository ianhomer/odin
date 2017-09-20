import { applyMiddleware, combineReducers, createStore } from 'redux'
import createSagaMiddleware from 'redux-saga'
import thunkMiddleware from 'redux-thunk'

import backend from './backend/mock.js'
import reducers from 'odin/reducers/index.js'
import { createLogger } from 'redux-logger'

function lastAction(state = null, action) {
  return action
}

reducers.lastAction = lastAction
const combinedReducers = combineReducers(reducers)

const sagaMiddleware = createSagaMiddleware()
const store = createStore(
  combinedReducers,
  applyMiddleware(
    thunkMiddleware,
    sagaMiddleware,
    createLogger({collapsed : false, timestamp : true, colors : {action : false}})
  )
)

sagaMiddleware.run(backend)

export default store