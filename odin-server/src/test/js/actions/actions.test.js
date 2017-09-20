import { createEntityRequested, deleteEntityRequested } from 'odin/actions/index.js'

import store from '../store'

function dispatchAndExpect(done, waitForActionType, action, assertion, message = '') {
  // TODO : Switch this invalid subscribe assertion to promise then
  console.log('dispatchAndExpect : ' + message)
  var unsubscriber = {}
  unsubscriber.unsubscribe = store.subscribe(() => {
    if (store.getState().lastAction.type === waitForActionType) {
      try {
        console.log(store.getState().entities.channel)
        console.log('dispatchAndExpect : assertion : ' + store.getState().lastAction.type + ':' + message)
        assertion()
        console.log('dispatchAndExpect : assertion OK : ' + message)
        unsubscriber.unsubscribe()
        done()
      } catch(e) {
        console.log('dispatchAndExpect : assertion NOK : ' + message)
        console.error(e)
      }
    }
  })
  store.dispatch(action())
}


describe('async actions', () => {
  test('Initial State', () => {
    expect(store.getState().entities['channel']).toBeUndefined()
  })

  test('Create Channel 1', (done) => {
    dispatchAndExpect(done, 'CREATE_ENTITY_SUCCEEDED', () => {
      return createEntityRequested(
        {
          name : 'test-name-1',
          number : 1
        }, 'channel'
      )
    }, () => {
      expect(store.getState().entities['channel'].entities.length).toBe(1)
    }, 'Create Channel 1')
  })

  test('Create Channel 2', (done) => {
    dispatchAndExpect(done, 'CREATE_ENTITY_SUCCEEDED', () => {
      return createEntityRequested({
        name : 'test-name-2',
        number : 2
      }, 'channel')
    }, () => {
      expect(store.getState().entities['channel'].entities.length).toBe(2)
    }, 'Create Channel 2')
  })

  test('Delete Channel 1', (done) => {
    dispatchAndExpect(done, 'DELETE_ENTITY_SUCCEEDED', () => {
      return deleteEntityRequested({
        name : 'test-name-1',
        number : 1
      }, 'channel')
    }, () => {
      expect(store.getState().entities['channel'].entities.length).toBe(1)
    }, 'Delete Channel 1')
  })
})