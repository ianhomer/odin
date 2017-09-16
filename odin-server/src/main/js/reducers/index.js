import { combineReducers } from 'redux'
import sequences from './sequences'
import channels from './channels'
import entities from './entities'

const reducers = combineReducers({
  channels,
  entities,
  sequences
})

export default reducers