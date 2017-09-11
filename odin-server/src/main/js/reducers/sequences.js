const sequences = (state = [], action) => {
  switch (action.type) {
  case 'ADD_SEQUENCE':
    return [
      ...state,
      {
        name : 'test-name',
        flowName : 'notation'
      }
    ]
  default:
    return state
  }
}

export default sequences