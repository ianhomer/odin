import { CREATE_ENTITY_SUCCEEDED, LOAD_ENTITIES_SUCCEEDED } from '../actions'

function entitiesAtPath(state = { entities: [] }, action) {
  switch (action.type) {
  case LOAD_ENTITIES_SUCCEEDED:
    return Object.assign({}, state, {
      entities: action.entities,
    })
  case CREATE_ENTITY_SUCCEEDED:
    return Object.assign({}, state, {
      entities: [ ...state.entities.filter( (item) => item.number != action.entity.number), action.entity ]
    })
  default:
    return state
  }
}

function entities(state = [], action) {
  switch (action.type) {
  case CREATE_ENTITY_SUCCEEDED:
  case LOAD_ENTITIES_SUCCEEDED:
    return Object.assign({}, state, {
      [action.path]: entitiesAtPath(state[action.path], action)
    })
  default:
    return state
  }
}

export default entities