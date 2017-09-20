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

export const DELETE_CHANNEL = 'DELETE_CHANNEL'
export const deleteChannel = (channel) => ({
  type: DELETE_CHANNEL,
  channel
})

export const LOAD_ENTITIES_REQUESTED = 'LOAD_ENTITIES_REQUESTED'
export function loadEntitiesRequested(path, schema) {
  return {
    type: LOAD_ENTITIES_REQUESTED,
    path,
    schema: schema
  }
}

export const LOAD_ENTITIES_SUCCEEDED = 'LOAD_ENTITIES_SUCCEEDED'
export const LOAD_ENTITIES_FAILED = 'LOAD_ENTITIES_FAILED'

export const RECEIVE_ENTITIES = 'RECEIVE_ENTITIES'
export function receiveEntities(path, entities) {
  return {
    type: RECEIVE_ENTITIES,
    path,
    entities: entities,
    receivedAt: Date.now()
  }
}

export const CREATE_ENTITY_REQUESTED = 'CREATE_ENTITY_REQUESTED'
export function createEntityRequested(entity, path) {
  return {
    type: CREATE_ENTITY_REQUESTED,
    path,
    entity : entity
  }
}

export const CREATE_ENTITY_SUCCEEDED = 'CREATE_ENTITY_SUCCEEDED'
export const CREATE_ENTITY_FAILED = 'CREATE_ENTITY_FAILED'
