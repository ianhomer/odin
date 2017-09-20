import { createStore, applyMiddleware } from 'redux'
import createSagaMiddleware from 'redux-saga'
import thunkMiddleware from 'redux-thunk'

import backend from './backend/mock.js'
import reducer from 'odin/reducers/index.js'
import { createLogger } from 'redux-logger'

const sagaMiddleware = createSagaMiddleware()
const store = createStore(
  reducer,
  applyMiddleware(
    thunkMiddleware,
    sagaMiddleware,
    createLogger({colors : {action : false}})
  )
)

sagaMiddleware.run(backend)

export default store