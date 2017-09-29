// Copyright (c) 2017 the original author or authors. All Rights Reserved
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

import React from 'react'
import { render } from 'react-dom'

import thunkMiddleware from 'redux-thunk'
import { createLogger } from 'redux-logger'
import { applyMiddleware, combineReducers, createStore } from 'redux'
import 'babel-polyfill'
import createSagaMiddleware from 'redux-saga'

import App from './containers/app'
import reducers from './reducers'
import { Flux } from './legacy/flux'
import { Backend } from './backend'
import { loadSchemaActions } from './actions'

// Create the backend integration layer
const backend = new Backend()

// Create saga middleware for handling of calls to backend.  Note that the backend is
// passed in as context for the saga middleware so that we can inject the backend API calls in.
// Injecting backend API calls in is required for switching in different backends as required
// for test cases which need a mock backend.
const sagaMiddleware = createSagaMiddleware({context: {backend: backend}})

// Create the application store
const store = createStore(
  combineReducers(reducers),
  applyMiddleware(
    thunkMiddleware,
    sagaMiddleware,
    createLogger({collapsed: true})
  )
)

// Hook in back end API calls
sagaMiddleware.run(backend.saga)

for (var i = 0; i < loadSchemaActions.length ; i++) {
  store.dispatch(loadSchemaActions[i])
}

// Render application
render(
  <App store={store} flux={new Flux()}/>,
  document.getElementById('root')
)