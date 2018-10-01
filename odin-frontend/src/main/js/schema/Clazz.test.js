import testSchemaJson from '../../../../target/api-snapshots/com/purplepip/odin/api/data/api/services/schema.json'
import testSequenceJson from '../../../../target/api-snapshots/com/purplepip/odin/api/data/api/rest/profile/sequence.json'
import {Schema} from './Schema'

describe('Clazz validation', () => {
  test('New Instance', () => {
    var schema = new Schema({
      'profiles': {'sequence': testSequenceJson},
      'performance': testSchemaJson
    })
    var clazz = schema.getClazz('flow-notation')
    // specific plugin shouldn't have general properties defined
    expect(clazz.hasProperty('properties')).toBe(false)
    // specific plugin should have performance defined
    expect(clazz.hasProperty('performance')).toBe(true)
    // specific plugin should have tick defined as required
    expect(clazz.hasProperty('tick')).toBe(true)
    expect(clazz.isRequired('tick')).toBe(true)
    // specific plugin should have triggers defined as not required
    expect(clazz.hasProperty('triggers')).toBe(true)
    expect(clazz.isRequired('triggers')).toBe(false)

    var refs = {
      'channel' : {'value': '1' },
      'enabled' : {'value': 'true' },
      'name': {'value': 'test-name'},
      'layers' : {'value': '' },
      'length': {'value': '1'},
      'format': {'value': 'easy'},
      'notation': {'value': 'C'},
      'offset': {'value': '1'},
      'performance': {'value': 'http://localhost:8080/api/rest/performance/1'},
      'tick.denominator': {'value': '1'},
      'tick.numerator': {'value': '1'},
      'tick.timeUnit': {'value': 'BEAT'},
      'triggers' : {'value': '' },
      'type': {'value': 'notation'}
    }
    var entity = clazz.newInstance(refs)
    expect(entity.name).toBe('test-name')
    expect(entity.performance).toBe('http://localhost:8080/api/rest/performance/1')
  })
})