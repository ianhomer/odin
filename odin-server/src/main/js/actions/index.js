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