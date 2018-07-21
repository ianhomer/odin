import {MockBackend} from './mock.js'
import {withQuery} from '../../../main/js/backend/withQuery'

describe('actions', () => {
  const backend = new MockBackend()

  test('Create URL', () => {
    const uri = withQuery('/test', {a: 1, b: 2})
    expect(uri).toBe('/test?a=1&b=2')
  })

  test('Fetch composition', () => {
    const composition = backend.fetchCompositionApi('C')
    expect(composition.measures[0].staves[0].voices[0].notation).toBe('C4/q, B4/h/r, B4/q/r')
  })
})
