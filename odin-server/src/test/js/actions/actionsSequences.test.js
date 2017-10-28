import TIMEOUT from '../constants.js'

import {CREATE_ENTITY_SUCCEEDED, FETCH_COMPOSITION_SUCCEEDED,
  createEntityRequested} from 'odin/actions/index.js'

import store from '../store'
import {dispatchAndExpect} from '../utils/dispatchAndExpect'

describe('Sequence async actions', () => {
  test('Initial State', () => {
    expect(store.getState().rest.sequence).toBeUndefined()
  })

  test('Create Notation 1', done => {
    dispatchAndExpect(store, done, [CREATE_ENTITY_SUCCEEDED, FETCH_COMPOSITION_SUCCEEDED],
      () => createEntityRequested({name: 'notation-1', typeName: 'notation', number: 1}, 'sequence'),
      () => {
        const sequences = store.getState().rest.sequence.entities
        expect(sequences.length).toBe(1)
        expect(sequences[0].name).toBe('notation-1')
        expect(sequences[0]._composition.measures[0].staves[0].voices[0].notation).toBe('C4/q, B4/h/r, B4/q/r')
      }
    )
  }, TIMEOUT)
})