// Copyright (c) 2017 Ian Homer. All Rights Reserved
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
import { createStore, applyMiddleware } from 'redux'
import 'babel-polyfill'
import createSagaMiddleware from 'redux-saga'

import App from './components/app'
import reducer from './reducers'
import { Flux } from './legacy/flux'
import backend from './backend'

// Create saga middleware for handling of calls to backend
const sagaMiddleware = createSagaMiddleware()

// Create the application store
const store = createStore(
  reducer,
  applyMiddleware(
    thunkMiddleware,
    sagaMiddleware,
    createLogger()
  )
)

// Hook in back end API calls
sagaMiddleware.run(backend)

// Render application
render(
  <App store={store} flux={new Flux()}/>,
  document.getElementById('root')
)