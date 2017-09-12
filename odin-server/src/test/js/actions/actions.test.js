import { createStore } from 'redux'
import reducers  from 'reducers/index.js'
import { addChannel, deleteChannel } from 'actions/index.js'

test('Actions OK', () => {
  let store = createStore(reducers)

  console.warn(store.getState())

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