import store from '../store'
import { LOAD_PROJECT_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED,
  loadProjectSchemaRequested, loadProfileSchemaRequested } from 'odin/actions/index.js'
import { dispatchAndExpect } from '../utils/dispatchAndExpect'
import { Schema } from 'odin/schema/schema'

describe('Schema validation', () => {

  test('Core Schema', done => {
    dispatchAndExpect(store, done, LOAD_PROJECT_SCHEMA_SUCCEEDED,
      () => loadProjectSchemaRequested(),
      () => {
        const schema = new Schema(store.getState().schema)
        var flowPatternSchema = schema.getClazzSchema('flow-pattern')
        expect(flowPatternSchema.title).toBe('Pattern')
      }
    )
  })

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
  })

  test('Load two profile schema', done => {
    dispatchAndExpect(store, done, LOAD_PROFILE_SCHEMA_SUCCEEDED,
      () => loadProfileSchemaRequested('channel'),
      () => {
        const schema = new Schema(store.getState().schema)
        expect(schema.getClazzSchema('sequence').title).toBe('Persistable sequence')
        expect(schema.getClazzSchema('channel').title).toBe('Persistable channel')
      }
    )
  })
})