import {Schema} from 'odin/schema/schema'

import testSchemaJson from './data/schema.json'
import testProjectJson from './data/project.json'
import testSequenceProfileSchemaJson from './data/api/profile/sequence.json'

export const testSchema = function() {
  var schema = new Schema(
    {
      project: testSchemaJson,
      profiles: {
        sequence: testSequenceProfileSchemaJson
      }
    })
  return schema
}()
export const testProject = testProjectJson