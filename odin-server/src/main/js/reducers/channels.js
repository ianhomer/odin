import { ADD_CHANNEL, DELETE_CHANNEL } from '../actions'

const channels = (state = [], action) => {
  switch (action.type) {
  case ADD_CHANNEL:
    return [
      ...state,
      action.channel
    ]
  case DELETE_CHANNEL:
    return state.filter( entity => entity.name !== action.channel.name )
  default:
    return state
  }
}

export default channels