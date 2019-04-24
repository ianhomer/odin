import {Schema} from '../../main/js/schema/Schema'

import testSchemaJson from '../../../target/api-snapshots/com/purplepip/odin/api/data/api/services/schema.json'
import testPerformanceJson from '../../../target/api-snapshots/com/purplepip/odin/api/data/api/rest/performance.json'
import testSequenceProfileSchemaJson from '../../../target/api-snapshots/com/purplepip/odin/api/data/api/rest/profile/sequence.json'

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