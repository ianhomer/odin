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
  UPDATE_ENTITY_REQUESTED, UPDATE_ENTITY_SUCCEEDED, UPDATE_ENTITY_FAILED,
  DELETE_ENTITY_REQUESTED, DELETE_ENTITY_SUCCEEDED, DELETE_ENTITY_FAILED,
  FETCH_COMPOSITION_REQUESTED, FETCH_COMPOSITION_SUCCEEDED, FETCH_COMPOSITION_FAILED,
  LOAD_ENTITIES_REQUESTED, LOAD_ENTITIES_SUCCEEDED, LOAD_ENTITIES_FAILED,
  LOAD_PROJECT_SCHEMA_REQUESTED, LOAD_PROJECT_SCHEMA_SUCCEEDED, LOAD_PROJECT_SCHEMA_FAILED,
  LOAD_PROFILE_SCHEMA_REQUESTED, LOAD_PROFILE_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_FAILED
} from '../actions'

const root = '/api'

const withQuery = function(uri, params) {
  return uri + (uri.indexOf('?') === -1 ? '?' : '&') +
    Object.keys(params)
      .map(key => encodeURIComponent(key) + '=' + encodeURIComponent(params[key]))
      .join('&')
}

export class Backend {
  createEntityApi(entity, path) {
    return fetch(root + '/' + path, {
      method : 'POST',
      headers: {
        'Content-Type': 'application/json'
      }, body : JSON.stringify(entity)
    })
      .then(response => response.json())
  }

  * createEntity(action) {
    try {
      const backend = yield getContext('backend')
      const entity = yield call(backend.createEntityApi, action.entity, action.path)
      yield put({type: CREATE_ENTITY_SUCCEEDED, entity: entity, path: action.path})
      yield call(backend.enrichEntity, entity)
    } catch (e) {
      yield put({type: CREATE_ENTITY_FAILED, message: e.message})
    }
  }

  updateEntityApi(entity) {
    return fetch(entity._links.self.href, {
      method : 'PUT',
      headers: {
        'Content-Type': 'application/json'
      }, body : JSON.stringify(entity)
    })
      .then(response => response.json())
  }

  * updateEntity(action) {
    try {
      const backend = yield getContext('backend')
      const entity = yield call(backend.updateEntityApi, action.entity)
      yield call(backend.enrichEntity, entity)
      yield put({type: UPDATE_ENTITY_SUCCEEDED, entity: entity, path: action.path})
    } catch (e) {
      yield put({type: UPDATE_ENTITY_FAILED, message: e.message})
    }
  }



  fetchCompositionApi(notation) {
    return fetch(withQuery('/services/composition', {notation}), {
      method : 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => response.json())
  }

  * fetchComposition(action) {
    try {
      const backend = yield getContext('backend')
      const composition = yield call(backend.fetchCompositionApi, action.entity)
      yield put({type: FETCH_COMPOSITION_SUCCEEDED, entity: action.entity, composition})
    } catch (e) {
      yield put({type: FETCH_COMPOSITION_FAILED, message: e.message})
    }
  }

  * enrichEntity(entity) {
    if (entity.flowName === 'notation') {
      yield put({type: FETCH_COMPOSITION_REQUESTED, entity})
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

  loadEntitiesApi(path) {
    return fetch(root + '/' + path, {
      method : 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => response.json())
      .then(json => json._embedded[path])
  }

  * loadEntities(action) {
    try {
      const backend = yield getContext('backend')
      const entities = yield call(backend.loadEntitiesApi, action.path, action.schema)
      entities.forEach(entity =>  backend.enrichEntity(entity))
      yield put({type: LOAD_ENTITIES_SUCCEEDED, entities: entities, path: action.path})
    } catch (e) {
      yield put({type: LOAD_ENTITIES_FAILED, message: e.message})
    }
  }

  loadProjectSchemaApi() {
    return fetch('/services/schema', {
      method : 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => response.json() )
  }

  * loadProjectSchema() {
    try {
      const backend = yield getContext('backend')
      const schema = yield call(backend.loadProjectSchemaApi)
      yield put({type: LOAD_PROJECT_SCHEMA_SUCCEEDED, schema: schema})
    } catch (e) {
      yield put({type: LOAD_PROJECT_SCHEMA_FAILED, message: e.message})
    }
  }

  loadProfileSchemaApi(path) {
    return fetch(root + '/profile/' + path, {
      method : 'GET',
      headers: {
        'Accept': 'application/schema+json'
      }
    })
      .then(response => response.json() )
  }

  * loadProfileSchema(action) {
    try {
      const backend = yield getContext('backend')
      const schema = yield call(backend.loadProfileSchemaApi, action.path)
      yield put({type: LOAD_PROFILE_SCHEMA_SUCCEEDED, schema: schema, path: action.path})
    } catch (e) {
      yield put({type: LOAD_PROFILE_SCHEMA_FAILED, message: e.message})
    }
  }

  * saga() {
    var backend = yield getContext('backend')
    yield takeEvery(CREATE_ENTITY_REQUESTED, backend.createEntity)
    yield takeEvery(UPDATE_ENTITY_REQUESTED, backend.updateEntity)
    yield takeEvery(DELETE_ENTITY_REQUESTED, backend.deleteEntity)
    yield takeEvery(FETCH_COMPOSITION_REQUESTED, backend.fetchComposition)
    yield takeEvery(LOAD_ENTITIES_REQUESTED, backend.loadEntities)
    yield takeEvery(LOAD_PROJECT_SCHEMA_REQUESTED, backend.loadProjectSchema)
    yield takeEvery(LOAD_PROFILE_SCHEMA_REQUESTED, backend.loadProfileSchema)
  }
}
