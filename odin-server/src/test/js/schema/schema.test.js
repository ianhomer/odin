import { testSchema } from '../testData.js'

test('Schema OK', () => {
  var notationFlowClass = testSchema.getFlowClazz('notation')
  expect(notationFlowClass.backEndClazz.id).toBe('sequence')
})