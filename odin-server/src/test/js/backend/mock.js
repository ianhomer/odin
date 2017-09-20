'use strict'

import { call, put, takeEvery } from 'redux-saga/effects'
import {
  CREATE_ENTITY_REQUESTED, CREATE_ENTITY_SUCCEEDED,
  DELETE_ENTITY_REQUESTED, DELETE_ENTITY_SUCCEEDED,
  LOAD_ENTITIES_REQUESTED, LOAD_ENTITIES_SUCCEEDED
} from 'odin/actions/index.js'

import testChannels from '../data/channel.json'

function createEntityApi(entity) {
  return entity
}

function* createEntity(action) {
  const entity = yield call(createEntityApi, action.entity)
  yield put({type: CREATE_ENTITY_SUCCEEDED, entity: entity, path: action.path})
}

function deleteEntityApi(entity) {
  return entity
}

function* deleteEntity(action) {
  const entity = yield call(deleteEntityApi, action.entity)
  yield put({type: DELETE_ENTITY_SUCCEEDED, entity: entity, path: action.path})
}

function loadJsonForPath(path) {
  switch (path) {
  case 'channel':
    return testChannels
  default:
    throw new Error('No test data available for path ' + path)
  }
}

function loadEntitiesApi(path) {
  // TODO : Load schema for path
  return loadJsonForPath(path)._embedded.channel
}

function* loadEntities(action) {
  const entities = yield call(loadEntitiesApi, action.path)
  yield put({type: LOAD_ENTITIES_SUCCEEDED, entities: entities, path: action.path})
}

export default function* backend() {
  yield takeEvery(CREATE_ENTITY_REQUESTED, createEntity)
  yield takeEvery(DELETE_ENTITY_REQUESTED, deleteEntity)
  yield takeEvery(LOAD_ENTITIES_REQUESTED, loadEntities)
}