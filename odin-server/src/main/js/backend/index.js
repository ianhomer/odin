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

'use strict'

import { call, put, takeEvery, getContext } from 'redux-saga/effects'
import fetch from 'isomorphic-fetch'
import {
  CREATE_ENTITY_REQUESTED, CREATE_ENTITY_SUCCEEDED, CREATE_ENTITY_FAILED,
  DELETE_ENTITY_REQUESTED, DELETE_ENTITY_SUCCEEDED, DELETE_ENTITY_FAILED,
  LOAD_ENTITIES_REQUESTED, LOAD_ENTITIES_SUCCEEDED, LOAD_ENTITIES_FAILED,
  LOAD_SCHEMA_REQUESTED, LOAD_SCHEMA_SUCCEEDED, LOAD_SCHEMA_FAILED,
} from '../actions'

const root = '/api'

export class Backend {
  createEntityApi(entity, path) {
    return fetch(root + '/' + path, {
      method : 'POST',
      headers: {
        'Content-Type': 'application/json'
      }, body : JSON.stringify(entity)
    })
      .then(response => response.json() )
  }

  * createEntity(action) {
    try {
      const backend = yield getContext('backend')
      const entity = yield call(backend.createEntityApi, action.entity, action.path)
      yield put({type: CREATE_ENTITY_SUCCEEDED, entity: entity, path: action.path})
    } catch (e) {
      yield put({type: CREATE_ENTITY_FAILED, message: e.message})
    }
  }

  deleteEntityApi(entity) {
    return fetch(entity._links.self.href, {
      method : 'DELETE',
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(() => entity)
  }

  * deleteEntity(action) {
    try {
      const backend = yield getContext('backend')
      const entity = yield call(backend.deleteEntityApi, action.entity)
      yield put({type: DELETE_ENTITY_SUCCEEDED, entity: entity, path: action.path})
    } catch (e) {
      yield put({type: DELETE_ENTITY_FAILED, message: e.message})
    }
  }

  loadEntitiesApi(path, schema) {
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

  * loadEntities(action) {
    try {
      const backend = yield getContext('backend')
      const entities = yield call(backend.loadEntitiesApi, action.path, action.schema)
      yield put({type: LOAD_ENTITIES_SUCCEEDED, entities: entities, path: action.path})
    } catch (e) {
      yield put({type: LOAD_ENTITIES_FAILED, message: e.message})
    }
  }

  loadSchemaApi() {
    return fetch('/services/schema', {
      method : 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => response.json() )
  }

  * loadSchema(action) {
    try {
      const backend = yield getContext('backend')
      const schema = yield call(backend.loadSchemaApi, action.entity, action.path)
      yield put({type: LOAD_SCHEMA_SUCCEEDED, schema: schema})
    } catch (e) {
      yield put({type: LOAD_SCHEMA_FAILED, message: e.message})
    }
  }

  * saga() {
    var backend = yield getContext('backend')
    yield takeEvery(CREATE_ENTITY_REQUESTED, backend.createEntity)
    yield takeEvery(DELETE_ENTITY_REQUESTED, backend.deleteEntity)
    yield takeEvery(LOAD_ENTITIES_REQUESTED, backend.loadEntities)
    yield takeEvery(LOAD_SCHEMA_REQUESTED, backend.loadSchema)
  }
}
