export function dispatchAndExpect(store, done, waitForActionType, action, assertion) {
  var asserter = { executed: false }
  var waitForActionTypes = waitForActionType instanceof Array
    ? [...waitForActionType] : [waitForActionType]
  asserter.unsubscribe = store.subscribe(() => {
    // The test redux store has a reducer that stores the last action so that we
    // can wait for the desired action type before executing the assertion
    waitForActionTypes = waitForActionTypes.filter(item => item !== store.getState().lastAction.type)
    if (waitForActionTypes.length == 0 && !asserter.executed) {
      // Set flag indicating that the assertion has been executed so that we don't apply
      // assertion multiple times.
      asserter.executed = true
      try {
        assertion()
        asserter.unsubscribe()
        done()
      } catch(e) {
        done.fail(e)
      }
    }
  })
  var request = action()
  if (request instanceof Array) {
    for (var i = 0; i < request.length ; i++) {
      store.dispatch(request[i])
    }
  } else {
    store.dispatch(request)
  }
}