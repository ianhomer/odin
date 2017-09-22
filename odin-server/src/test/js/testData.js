import { MockFlux } from './flux/mockFlux.js'
import { Schema } from 'odin/schema/schema'

import testSchemaJson from './data/schema.json'
import testProjectJson from './data/project.json'
import testSequenceProfileSchemaJson from './data/profile/sequence.json'

export const mockFlux = new MockFlux()
export const testSchema = function() {
  var schema = new Schema(
    {
      project: testSchemaJson,
      profiles: {
        sequence: testSequenceProfileSchemaJson
      }
    }, mockFlux)
  return schema
}()
export const testProject = testProjectJson