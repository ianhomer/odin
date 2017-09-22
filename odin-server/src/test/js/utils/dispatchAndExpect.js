export function dispatchAndExpect(store, done, waitForActionType, action, assertion) {
  var unsubscriber = {}
  unsubscriber.unsubscribe = store.subscribe(() => {
    // The test redux store has a reducer that stores the last action so that we
    // can wait for the desired action type before executing the assertion
    if (store.getState().lastAction.type === waitForActionType) {
      try {
        assertion()
        unsubscriber.unsubscribe()
        done()
      } catch(e) {
        done.fail(e)
      }
    }
  })
  var request = action()
  if (request.constructor === Array) {
    for (var i = 0; i < request.length ; i++) {
      store.dispatch(request[i])
    }
  } else {
    store.dispatch(request)
  }
}