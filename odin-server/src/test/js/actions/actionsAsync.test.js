import { createEntityRequested, deleteEntityRequested } from 'odin/actions/index.js'

import store from '../store'
import { dispatchAndExpect } from '../utils/dispatchAndExpect'

describe('async actions', () => {
  test('Initial State', () => {
    expect(store.getState().entities['channel']).toBeUndefined()
  })

  test('Create Channel 1', done => {
    dispatchAndExpect(store, done, 'CREATE_ENTITY_SUCCEEDED',
      () => createEntityRequested({ name : 'test-name-1', number : 1}, 'channel'),
      () => expect(store.getState().entities['channel'].entities.length).toBe(1)
    )
  })

  test('Create Channel 2', done => {
    dispatchAndExpect(store, done, 'CREATE_ENTITY_SUCCEEDED',
      () => createEntityRequested({ name : 'test-name-2', number : 2}, 'channel'),
      () => expect(store.getState().entities['channel'].entities.length).toBe(2)
    )
  })

  test('Delete Channel 1', done => {
    dispatchAndExpect(store, done, 'DELETE_ENTITY_SUCCEEDED',
      () => deleteEntityRequested({ name : 'test-name-1', number : 1,
        _links : { self : { href : 'http://localhost:8080/api/channel/7' }}}, 'channel'),
      () => expect(store.getState().entities['channel'].entities.length).toBe(1)
    )
  })
})