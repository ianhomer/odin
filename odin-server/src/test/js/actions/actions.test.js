import { createStore } from 'redux'
import reducers  from 'reducers/index.js'
import { addChannel } from 'actions/index.js'

let store = createStore(reducers)

test('Actions OK', () => {
  console.log(store.getState())

  // Every time the state changes, log it
  // Note that subscribe() returns a function for unregistering the listener
  let unsubscribe = store.subscribe(() =>
    console.log(store.getState())
  )

  store.dispatch(
    addChannel(
      {
        name : 'test-name',
        channel : 1
      }
    )
  )
})