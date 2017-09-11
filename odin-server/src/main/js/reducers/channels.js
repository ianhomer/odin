import { ADD_CHANNEL } from '../actionTypes';

const channels = (state = [], action) => {
  switch (action.type) {
  case ADD_CHANNEL:
    return [
      ...state,
      {
        name : 'piano',
        channel : 1
      }
    ];
  default:
    return state;
  }
};

export default channels;