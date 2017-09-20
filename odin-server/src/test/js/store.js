import { createStore, applyMiddleware } from 'redux'
import createSagaMiddleware from 'redux-saga'
import thunkMiddleware from 'redux-thunk'

import backend from './backend/mock.js'
import reducer from 'odin/reducers/index.js'

const sagaMiddleware = createSagaMiddleware()
const store = createStore(
  reducer,
  applyMiddleware(
    thunkMiddleware,
    sagaMiddleware
  )
)

sagaMiddleware.run(backend)

export default store