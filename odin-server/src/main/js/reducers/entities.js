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

import { CREATE_ENTITY_SUCCEEDED, DELETE_ENTITY_REQUESTED, LOAD_ENTITIES_SUCCEEDED } from '../actions'

function getEntityFilter(action) {
  if (action.path === 'channel') {
    // Channels merge based on the channel number
    return (item) => { return item.number != action.entity.number }
  } else {
    return () => { true }
  }
}

function entitiesAtPath(state = { entities: [] }, action) {
  switch (action.type) {
  case LOAD_ENTITIES_SUCCEEDED:
    return Object.assign({}, state, {
      entities: action.entities,
    })
  case DELETE_ENTITY_REQUESTED:
    return Object.assign({}, state, {
      entities: [ ...state.entities.filter(getEntityFilter(action)) ]
    })
  case CREATE_ENTITY_SUCCEEDED:
    return Object.assign({}, state, {
      entities: [ ...state.entities.filter(getEntityFilter(action)), action.entity ]
    })
  default:
    return state
  }
}

function entities(state = [], action) {
  switch (action.type) {
  case CREATE_ENTITY_SUCCEEDED:
  case DELETE_ENTITY_REQUESTED:
  case LOAD_ENTITIES_SUCCEEDED:
    return Object.assign({}, state, {
      [action.path]: entitiesAtPath(state[action.path], action)
    })
  default:
    return state
  }
}

export default entities