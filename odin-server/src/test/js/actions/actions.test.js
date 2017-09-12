import { createStore } from 'redux'
import reducers  from 'odin/reducers/index.js'
import { addChannel, deleteChannel } from 'odin/actions/index.js'

test('Actions OK', () => {
  let store = createStore(reducers)

  // Every time the state changes, log it
  // Note that subscribe() returns a function for unregistering the listener
  let unsubscribe = store.subscribe(() =>
    console.warn(store.getState())
  )

  expect(store.getState().channels.length).toBe(0)
  store.dispatch(
    addChannel({
      name : 'test-name-1',
      channel : 1
    })
  )
  expect(store.getState().channels.length).toBe(1)

  store.dispatch(
    addChannel({
      name : 'test-name-2',
      channel : 1
    })
  )
  expect(store.getState().channels.length).toBe(2)

  store.dispatch(
    deleteChannel({
      name : 'test-name-1',
      channel : 1
    })
  )
  expect(store.getState().channels.length).toBe(1)

  unsubscribe()
})