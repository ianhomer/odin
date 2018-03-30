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



import {call, put, takeEvery, getContext} from 'redux-saga/effects'
import fetch from 'isomorphic-fetch'
import {
  CREATE_ENTITY_REQUESTED, CREATE_ENTITY_SUCCEEDED, CREATE_ENTITY_FAILED,
  UPDATE_ENTITY_REQUESTED, UPDATE_ENTITY_SUCCEEDED, UPDATE_ENTITY_FAILED,
  DELETE_ENTITY_REQUESTED, DELETE_ENTITY_SUCCEEDED, DELETE_ENTITY_FAILED,
  PATCH_ENTITY_REQUESTED, PATCH_ENTITY_SUCCEEDED, PATCH_ENTITY_FAILED,
  FETCH_COMPOSITION_REQUESTED, FETCH_COMPOSITION_SUCCEEDED, FETCH_COMPOSITION_FAILED,
  FETCH_SYSTEM_REQUESTED, FETCH_SYSTEM_SUCCEEDED, FETCH_SYSTEM_FAILED,
  LOAD_ENTITIES_REQUESTED, LOAD_ENTITIES_SUCCEEDED, LOAD_ENTITIES_FAILED,
  LOAD_PERFORMANCE_SCHEMA_REQUESTED, LOAD_PERFORMANCE_SCHEMA_SUCCEEDED, LOAD_PERFORMANCE_SCHEMA_FAILED,
  LOAD_PROFILE_SCHEMA_REQUESTED, LOAD_PROFILE_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_FAILED
} from '../actions'
import {withQuery} from './withQuery'

const apiRoot = '/api'
const restRoot = apiRoot + '/rest'

export class Backend {
  createEntityApi(entity, path) {
    return fetch(restRoot + '/' + path, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }, body: JSON.stringify(entity)
    })
      .then(response => response.json())
      .catch(reason => console.error(reason))
  }

  *createEntity(action) {
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
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      }, body: JSON.stringify(entity)
    })
      .then(response => response.json())
      .catch(reason => console.error(reason))
  }

  *updateEntity(action) {
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
    return fetch(withQuery(apiRoot + '/services/composition', {notation}), {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => response.json())
      .catch(reason => console.error(reason))
  }

  *fetchComposition(action) {
    try {
      const backend = yield getContext('backend')
      const composition = yield call(backend.fetchCompositionApi, action.notation)
      yield put({type: FETCH_COMPOSITION_SUCCEEDED, entityName: action.entityName,
        notation: action.notation, composition})
    } catch (e) {
      yield put({type: FETCH_COMPOSITION_FAILED, message: e.message})
    }
  }

  fetchSystemApi(path) {
    return fetch(apiRoot + '/services/system/' + path, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => response.json())
      .catch(reason => console.error(reason))
  }

  *fetchSystem(action) {
    try {
      const backend = yield getContext('backend')
      const system = yield call(backend.fetchSystemApi, action.path)
      yield put({type: FETCH_SYSTEM_SUCCEEDED, path: action.path, system})
    } catch (e) {
      yield put({type: FETCH_SYSTEM_FAILED, message: e.message})
    }
  }

  *enrichEntity(entity) {
    if (entity.type === 'notation') {
      yield put({type: FETCH_COMPOSITION_REQUESTED, entityName: entity.name, notation: entity.properties.notation})
    }
  }

  deleteEntityApi(entity) {
    return fetch(entity._links.self.href, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(() => entity)
      .catch(reason => console.error(reason))
  }

  *deleteEntity(action) {
    try {
      const backend = yield getContext('backend')
      const entity = yield call(backend.deleteEntityApi, action.entity)
      yield put({type: DELETE_ENTITY_SUCCEEDED, entity: entity, path: action.path})
    } catch (e) {
      yield put({type: DELETE_ENTITY_FAILED, message: e.message})
    }
  }

  patchEntityApi(entity, patch) {
    return fetch(entity._links.self.href, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json-patch+json'
        // TODO : Etag support, note that entity needs to be loaded from server prior to
        // editing to populate Etag
        //'If-Match': entity.headers.Etag
      },
      body: JSON.stringify(patch)
    })
      .then(response => response.json())
      .catch(reason => console.error(reason))
  }

  *patchEntity(action) {
    try {
      const backend = yield getContext('backend')
      const entity = yield call(backend.patchEntityApi, action.entity, action.patch)
      yield put({type: PATCH_ENTITY_SUCCEEDED, entity: entity, path: action.path})
      // Support chain of patches
      if (action.nextAction) {
        yield put(action.nextAction)
      }
    } catch (e) {
      yield put({type: PATCH_ENTITY_FAILED, message: e.message})
    }
  }

  loadEntitiesApi(path) {
    return fetch(restRoot + '/' + path, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => response.json())
      .then(json => json._embedded[path])
      .catch(reason => console.error(reason))
  }

  *loadEntities(action) {
    try {
      const backend = yield getContext('backend')
      const entities = yield call(backend.loadEntitiesApi, action.path, action.schema)
      for (var i = 0; i < entities.length ; i++) {
        yield call(backend.enrichEntity, entities[i])
        // Set the path value in the entity so that the front end knows the type of entity.
        entities[i].path = action.path
      }
      yield put({type: LOAD_ENTITIES_SUCCEEDED, entities: entities, path: action.path})
    } catch (e) {
      yield put({type: LOAD_ENTITIES_FAILED, message: e.message})
    }
  }

  loadPerformanceSchemaApi() {
    return fetch('/api/services/schema', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => response.json())
      .catch(reason => console.error(reason))
  }

  *loadPerformanceSchema() {
    try {
      const backend = yield getContext('backend')
      const schema = yield call(backend.loadPerformanceSchemaApi)
      yield put({type: LOAD_PERFORMANCE_SCHEMA_SUCCEEDED, schema: schema})
    } catch (e) {
      yield put({type: LOAD_PERFORMANCE_SCHEMA_FAILED, message: e.message})
    }
  }

  loadProfileSchemaApi(path) {
    return fetch(restRoot + '/profile/' + path, {
      method: 'GET',
      headers: {
        'Accept': 'application/schema+json'
      }
    })
      .then(response => response.json())
      .catch(reason => console.error(reason))
  }

  *loadProfileSchema(action) {
    try {
      const backend = yield getContext('backend')
      const schema = yield call(backend.loadProfileSchemaApi, action.path)
      yield put({type: LOAD_PROFILE_SCHEMA_SUCCEEDED, schema: schema, path: action.path})
    } catch (e) {
      yield put({type: LOAD_PROFILE_SCHEMA_FAILED, message: e.message})
    }
  }

  *saga() {
    var backend = yield getContext('backend')
    yield takeEvery(CREATE_ENTITY_REQUESTED, backend.createEntity)
    yield takeEvery(UPDATE_ENTITY_REQUESTED, backend.updateEntity)
    yield takeEvery(DELETE_ENTITY_REQUESTED, backend.deleteEntity)
    yield takeEvery(PATCH_ENTITY_REQUESTED, backend.patchEntity)
    yield takeEvery(FETCH_COMPOSITION_REQUESTED, backend.fetchComposition)
    yield takeEvery(FETCH_SYSTEM_REQUESTED, backend.fetchSystem)
    yield takeEvery(LOAD_ENTITIES_REQUESTED, backend.loadEntities)
    yield takeEvery(LOAD_PERFORMANCE_SCHEMA_REQUESTED, backend.loadPerformanceSchema)
    yield takeEvery(LOAD_PROFILE_SCHEMA_REQUESTED, backend.loadProfileSchema)
  }
}
