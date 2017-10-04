import TIMEOUT from '../constants.js'

import {CREATE_ENTITY_SUCCEEDED, PATCH_ENTITY_SUCCEEDED,
  moveLayerRequested, removeLayerRequested,
  createEntityRequested} from 'odin/actions/index.js'


import store from '../store'
import {dispatchAndExpect} from '../utils/dispatchAndExpect'

function getLayer(name) {
  return store.getState().collections['layer'].entities.find(layer => layer.name === name)
}

describe('Layer async actions', () => {
  test('Initial State', () => {
    expect(store.getState().collections['sequence']).toBeUndefined()
  })

  test('Create Layers', done => {
    dispatchAndExpect(store, done, [CREATE_ENTITY_SUCCEEDED, CREATE_ENTITY_SUCCEEDED, CREATE_ENTITY_SUCCEEDED],
      () => [
        createEntityRequested({name: 'layer-1'}, 'layer'),
        createEntityRequested({name: 'layer-2'}, 'layer'),
        createEntityRequested({name: 'layer-3'}, 'layer')
      ],
      () => {
        const layers = store.getState().collections['layer'].entities
        expect(layers[0].name).toBe('layer-1')
        expect(layers.length).toBe(3)
      }
    )
  }, TIMEOUT)

  test('Move Layer from root', done => {
    dispatchAndExpect(store, done, [PATCH_ENTITY_SUCCEEDED],
      () => moveLayerRequested(getLayer('layer-1'), null, getLayer('layer-3')),
      () => {
        expect(getLayer('layer-1').layers[0]).toBe('layer-3')
        expect(getLayer('layer-2').layers.length).toBe(0)
      }
    )
  }, TIMEOUT)

  test('Move Layer from one layer to another', done => {
    dispatchAndExpect(store, done, [PATCH_ENTITY_SUCCEEDED, PATCH_ENTITY_SUCCEEDED],
      () => moveLayerRequested(getLayer('layer-2'), getLayer('layer-1'), getLayer('layer-3')),
      () => {
        expect(getLayer('layer-1').layers.length).toBe(0)
        expect(getLayer('layer-2').layers[0]).toBe('layer-3')
      }
    )
  }, TIMEOUT)

  test('Remove layer from one layer', done => {
    dispatchAndExpect(store, done, [PATCH_ENTITY_SUCCEEDED],
      () => removeLayerRequested(getLayer('layer-2'), getLayer('layer-3')),
      () => {
        expect(getLayer('layer-1').layers.length).toBe(0)
        expect(getLayer('layer-2').layers.length).toBe(0)
      }
    )
  }, TIMEOUT)
})