import { CREATE_ENTITY, RECEIVE_ENTITIES } from '../actions'

function entitiesAtPath(state = { entities: [] }, action) {
  switch (action.type) {
  case RECEIVE_ENTITIES:
    return Object.assign({}, state, {
      entities: action.entities,
    })
  case CREATE_ENTITY:
    return Object.assign({}, state, {
      entities: [ ...state.entities.filter( (item) => item.number != action.entity.number), action.entity ]
    })
  default:
    return state
  }
}

function entities(state = [], action) {
  switch (action.type) {
  case CREATE_ENTITY:
  case RECEIVE_ENTITIES:
    return Object.assign({}, state, {
      [action.path]: entitiesAtPath(state[action.path], action)
    })
  default:
    return state
  }
}

export default entities