import TIMEOUT from '../constants.js'

import store from '../../../test/js/store'
import {LOAD_PERFORMANCE_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED,
  loadPerformanceSchemaRequested, loadProfileSchemaRequested} from '../actions/index.js'
import {dispatchAndExpect} from '../../../test/js/utils/dispatchAndExpect'
import {Schema} from './Schema'

describe('Schema validation', () => {

  test('Core Schema', done => {
    dispatchAndExpect(store, done, LOAD_PERFORMANCE_SCHEMA_SUCCEEDED,
      () => loadPerformanceSchemaRequested(),
      () => {
        const schema = new Schema(store.getState().schema)
        var flowPatternSchema = schema.getClazzSchema('flow-pattern')
        expect(flowPatternSchema.title).toBe('Pattern')
      }
    )
  }, TIMEOUT)

  test('Load profile schema', done => {
    dispatchAndExpect(store, done, LOAD_PROFILE_SCHEMA_SUCCEEDED,
      () => loadProfileSchemaRequested('sequence'),
      () => {
        const schema = new Schema(store.getState().schema)
        expect(schema.getClazzSchema('sequence').title).toBe('Persistable sequence')
        const notationFlowClass = schema.getFlowClazz('notation')
        expect(notationFlowClass.getBackEndClazz().id).toBe('sequence')
      }
    )
  }, TIMEOUT)

  test('Load two profile schema', done => {
    dispatchAndExpect(store, done, LOAD_PROFILE_SCHEMA_SUCCEEDED,
      () => loadProfileSchemaRequested('channel'),
      () => {
        const schema = new Schema(store.getState().schema)
        expect(schema.getClazzSchema('sequence').title).toBe('Persistable sequence')
        expect(schema.getClazzSchema('channel').title).toBe('Persistable channel')
      }
    )
  }, TIMEOUT)
})