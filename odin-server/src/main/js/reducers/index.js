import { combineReducers } from 'redux'
import sequences from './sequences'
import channels from './channels'

const reducers = combineReducers({
  channels,
  sequences
})

export default reducers