import { ADD_CHANNEL, DELETE_CHANNEL } from '../actionTypes'

export const addChannel = (channel) => ({
  type: ADD_CHANNEL,
  channel
})

export const deleteChannel = (channel) => ({
  type: DELETE_CHANNEL,
  channel
})