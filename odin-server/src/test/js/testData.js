import { MockFlux } from './flux/mockFlux.js'
import { Schema } from 'odin/schema/schema'

import testSchemaJson from './data/schema.json'
import testProjectJson from './data/project.json'
import testSequenceProfileSchemaJson from './data/profile/sequence.json'

export const mockFlux = new MockFlux()
export const testSchema = function() {
  var schema = new Schema(testSchemaJson, mockFlux)
  schema.addSchemaForClazz(testSequenceProfileSchemaJson, 'sequence')
  return schema
}()
export const testProject = testProjectJson