import {Schema} from 'odin/schema/schema'

import testSchemaJson from './data/schema.json'
import testPerformanceJson from './data/performance.json'
import testSequenceProfileSchemaJson from './data/api/profile/sequence.json'

export const testSchema = function() {
  var schema = new Schema(
    {
      performance: testSchemaJson,
      profiles: {
        sequence: testSequenceProfileSchemaJson
      }
    })
  return schema
}()
export const testPerformance = testPerformanceJson