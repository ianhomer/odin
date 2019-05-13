// Copyright (c) 2017 The Odin Authors. All Rights Reserved
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import thunkMiddleware from 'redux-thunk'
import {createLogger} from 'redux-logger'
import {applyMiddleware, combineReducers, createStore} from 'redux'
import createSagaMiddleware from 'redux-saga'

import reducers from './reducers'
import {Backend} from './backend'
import {loadSchemaActions} from './actions'

// Create the backend integration layer
const backend = new Backend()

// Create saga middleware for handling of calls to backend.  Note that the backend is
// passed in as context for the saga middleware so that we can inject the backend API calls in.
// Injecting backend API calls in is required for switching in different backends as required
// for test cases which need a mock backend.
const sagaMiddleware = createSagaMiddleware({context: {backend: backend}})

const middleware = [thunkMiddleware, sagaMiddleware]
const env = process.env.NODE_ENV || 'dev'
if (env === 'dev') {
  middleware.push(createLogger({collapsed: true}))
}

// Create the application store
const store = createStore(
  combineReducers(reducers),
  applyMiddleware(...middleware)
)

// Hook in back end API calls
sagaMiddleware.run(backend.saga)

for (let i = 0; i < loadSchemaActions.length ; i++) {
  store.dispatch(loadSchemaActions[i])
}

export default store
