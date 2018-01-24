import testSchemaJson from '../data/services/schema.json'
import testSequenceJson from '../data/api/profile/sequence.json'
import {Schema} from 'odin/schema/Schema'

describe('Clazz validation', () => {
  test('New Instance', () => {
    var schema = new Schema({
      'profiles': {'sequence': testSequenceJson},
      'performance': testSchemaJson
    })
    var notationClazz = schema.getClazz('flow-notation')
    var refs = {
      'name': {'value': 'test-name'},
      'performance': {'value': 'http://localhost:8080/api/performance/1'}
    }
    var entity = notationClazz.newInstance(refs)
    expect(entity.name).toBe('test-name')
    //expect(entity.performance).toBe('http://localhost:8080/api/performance/1')
  })
})