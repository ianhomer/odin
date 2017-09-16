import { call, put, takeEvery, takeLatest } from 'redux-saga/effects'
import fetch from 'isomorphic-fetch'
import { CREATE_ENTITY_REQUESTED, CREATE_ENTITY_SUCCEEDED, CREATE_ENTITY_FAILED } from '../actions'

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
      const entity = yield call(createEntityApi, action.entity, action.path);
      yield put({type: CREATE_ENTITY_SUCCEEDED, entity: entity, path: action.path});
   } catch (e) {
      yield put({type: CREATE_ENTITY_FAILED, message: e.message});
   }
}

function* backend() {
  yield takeEvery(CREATE_ENTITY_REQUESTED, createEntity);
}

export default backend