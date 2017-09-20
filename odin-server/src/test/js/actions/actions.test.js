import { createEntityRequested, deleteEntityRequested } from 'odin/actions/index.js'

import store from '../store'

function dispatchAndExpect(action, assertion) {
  // TODO : Switch this invalid subscribe assertion to promise then all do done() properly
  var done = true
  var unsubscribe = store.subscribe(() => {
    setTimeout(() => {
      assertion()
      done = true
      done()
    }, 10000)
  })
  store.dispatch(action())
  unsubscribe()
  expect(done).toBeTruthy()
}


describe('async actions', () => {
  test('Initial State', () => {
    expect(store.getState().channels.length).toBe(0)
  })

  test('Create Channel 1', () => {
    dispatchAndExpect(() => {
      return createEntityRequested({
        name : 'test-name-1',
        channel : 1
      })
    }, () => {
      expect(store.getState().channels.length).toBe(1)
    })
  })

  test('Create Channel 2', () => {
    dispatchAndExpect(() => {
      return createEntityRequested({
        name : 'test-name-2',
        channel : 2
      })
    }, () => {
      expect(store.getState().channels.length).toBe(2)
    })
  })

  test('Delete Channel 1', () => {
    dispatchAndExpect(() => {
      return deleteEntityRequested({
        name : 'test-name-1',
        channel : 1
      })
    }, () => {
      expect(store.getState().channels.length).toBe(1)
    })
  })
})