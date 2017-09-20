import { deleteEntityRequested } from 'odin/actions/index.js'

describe('actions', () => {
  test('Delete Channel Action', () => {
    var action = deleteEntityRequested({
      name : 'test-name-1',
      number : 1,
      _links : { self : { href : 'http://localhost:8080/api/channel/7' }}
    })
    expect(action.path).toBe('channel')
  })
})