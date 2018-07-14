import {Schema} from 'odin/schema/Schema'

import testSchemaJson from 'odin-api-snapshot/data/api/services/schema.json'
import testPerformanceJson from 'odin-api-snapshot/data/api/rest/performance.json'
import testSequenceProfileSchemaJson from 'odin-api-snapshot/data/api/rest/profile/sequence.json'

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