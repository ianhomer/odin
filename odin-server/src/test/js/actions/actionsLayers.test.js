import TIMEOUT from '../constants.js'

import {CREATE_ENTITY_SUCCEEDED,
  createEntityRequested} from 'odin/actions/index.js'

import store from '../store'
import {dispatchAndExpect} from '../utils/dispatchAndExpect'

describe('Layer async actions', () => {
  test('Initial State', () => {
    expect(store.getState().collections['sequence']).toBeUndefined()
  })

  test('Create Layers', done => {
    dispatchAndExpect(store, done, [CREATE_ENTITY_SUCCEEDED, CREATE_ENTITY_SUCCEEDED],
      () => [
        createEntityRequested({name: 'layer-1'}, 'layer'),
        createEntityRequested({name: 'layer-2'}, 'layer')
      ],
      () => {
        const layers = store.getState().collections['layer'].entities
        expect(layers[0].name).toBe('layer-1')
        expect(layers.length).toBe(2)
      }
    )
  }, TIMEOUT)
})