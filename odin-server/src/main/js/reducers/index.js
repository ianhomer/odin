import { combineReducers } from 'redux';
import sequences from './sequences';
import channels from './channels';

const combinedReducers = combineReducers({
  channels,
  sequences
});

export default combinedReducers;