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
