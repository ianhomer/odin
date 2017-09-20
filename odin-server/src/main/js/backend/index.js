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

'use strict'

import { call, put, takeEvery } from 'redux-saga/effects'
import fetch from 'isomorphic-fetch'
import {
  CREATE_ENTITY_REQUESTED, CREATE_ENTITY_SUCCEEDED, CREATE_ENTITY_FAILED,
  LOAD_ENTITIES_REQUESTED, LOAD_ENTITIES_SUCCEEDED, LOAD_ENTITIES_FAILED
} from '../actions'

const root = '/api'

function createEntityApi(entity, path) {
  return fetch(root + '/' + path, {
    method : 'POST',
    headers: {
      'Content-Type': 'application/json'
    }, body : JSON.stringify(entity)
  })
    .then(response => response.json() )
}

function* createEntity(action) {
  try {
    const entity = yield call(createEntityApi, action.entity, action.path)
    yield put({type: CREATE_ENTITY_SUCCEEDED, entity: entity, path: action.path})
  } catch (e) {
    yield put({type: CREATE_ENTITY_FAILED, message: e.message})
  }
}

function loadEntitiesApi(path, schema) {
  schema.loadClazz(path).then(() => {})
  return fetch(root + '/' + path, {
    method : 'GET',
    headers: {
      'Content-Type': 'application/json'
    }
  })
    .then(response => response.json())
    .then(json => json._embedded.channel)
}

function* loadEntities(action) {
  try {
    const entities = yield call(loadEntitiesApi, action.path, action.schema)
    yield put({type: LOAD_ENTITIES_SUCCEEDED, entities: entities, path: action.path})
  } catch (e) {
    yield put({type: LOAD_ENTITIES_FAILED, message: e.message})
  }
}

export default function* backend() {
  yield takeEvery(CREATE_ENTITY_REQUESTED, createEntity)
  yield takeEvery(LOAD_ENTITIES_REQUESTED, loadEntities)
}