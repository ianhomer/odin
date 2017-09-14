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

// Fetch entities actions

const crud = require('../crud')

export const REQUEST_ENTITIES = 'REQUEST_ENTITIES'
function requestEntities(path) {
  return {
    type: REQUEST_ENTITIES,
    path
  }
}

export const RECEIVE_ENTITIES = 'RECEIVE_ENTITIES'
function receiveEntities(path, schema, entities) {
  return {
    type: RECEIVE_ENTITIES,
    path,
    channels: entities,
    receivedAt: Date.now()
  }
}

export function fetchEntities(path, schema) {
  return function (dispatch) {
    dispatch(requestEntities(path))
    return crud.loadFromServer(path, schema, (entities) => { dispatch(receiveEntities(path, entities)) } )
  }
}