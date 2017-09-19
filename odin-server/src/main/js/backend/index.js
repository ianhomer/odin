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