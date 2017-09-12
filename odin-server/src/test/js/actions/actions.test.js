import { createStore } from 'redux'
import reducers  from 'reducers/index.js'
import { addChannel } from 'actions/index.js'

test('Actions OK', () => {
  let store = createStore(reducers)

  console.warn(store.getState())

  // Every time the state changes, log it
  // Note that subscribe() returns a function for unregistering the listener
  let unsubscribe = store.subscribe(() =>
    console.warn(store.getState())
  )

  store.dispatch(
    addChannel(
      {
        name : 'test-name',
        channel : 1
      }
    )
  )

  unsubscribe()
})