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

import {CREATE_ENTITY_SUCCEEDED, UPDATE_ENTITY_SUCCEEDED,
  DELETE_ENTITY_SUCCEEDED, PATCH_ENTITY_SUCCEEDED, LOAD_ENTITIES_SUCCEEDED,
  FETCH_COMPOSITION_SUCCEEDED} from '../actions'

function getEntityFilter(action) {
  if (action.path === 'channel') {
    // Channels merge based on the channel number
    return entity => { return entity.number != action.entity.number }
  } else {
    return entity => { return entity._links.self.href != action.entity._links.self.href }
  }
}

function comparator(a, b) {
  if (a.name < b.name) {
    return -1
  }
  return a.name > b.name ? 1 : 0
}

function collectionsAtPath(state = {entities: [], newEntities: {}}, action) {
  switch (action.type) {
  case LOAD_ENTITIES_SUCCEEDED:
    return Object.assign({}, state, {
      entities: action.entities.sort(comparator)
    })
  case DELETE_ENTITY_SUCCEEDED:
    return Object.assign({}, state, {
      entities: [...state.entities.filter(getEntityFilter(action))].sort(comparator)
    })
  case CREATE_ENTITY_SUCCEEDED:
  case UPDATE_ENTITY_SUCCEEDED:
  case PATCH_ENTITY_SUCCEEDED:
    return Object.assign({}, state, {
      entities: [...state.entities.filter(getEntityFilter(action)), action.entity].sort(comparator)
    })
  case FETCH_COMPOSITION_SUCCEEDED:
    if (action.entityName === undefined) {
      // Store composition for new entity that is being created
      return Object.assign({}, state, {
        entities: state.entities.sort(comparator),
        newEntities: {
          notation: {
            properties: {notation: action.notation},
            _composition: action.composition
          }
        }
      })
    } else {
      return Object.assign({}, state, {
        entities: [...state.entities.map(entity => {
          // Add the composition object to the sequence entity
          if (action.entityName === entity.name) {
            entity._composition = action.composition
          }
          return entity
        })].sort(comparator)
      })
    }
  default:
    return state
  }
}

function collections(state = [], action) {
  switch (action.type) {
  case CREATE_ENTITY_SUCCEEDED:
  case UPDATE_ENTITY_SUCCEEDED:
  case DELETE_ENTITY_SUCCEEDED:
  case LOAD_ENTITIES_SUCCEEDED:
  case PATCH_ENTITY_SUCCEEDED:
    return Object.assign({}, state, {
      [action.path]: collectionsAtPath(state[action.path], action)
    })
  case FETCH_COMPOSITION_SUCCEEDED:
    return Object.assign({}, state, {
      sequence: collectionsAtPath(state['sequence'], action)
    })
  default:
    return state
  }
}

export default collections