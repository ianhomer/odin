'use strict'

export const ADD_CHANNEL = 'ADD_CHANNEL'
export const addChannel = (channel) => ({
  type: ADD_CHANNEL,
  channel
})

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
