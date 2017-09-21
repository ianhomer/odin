import React from 'react'
import { shallow } from 'enzyme'

import App from 'odin/containers/app.js'
import { loadSchemaRequested } from 'odin/actions/index.js'

import { mockFlux } from '../testData.js'
import store from '../store'
import { dispatchAndExpect } from '../utils/dispatchAndExpect'

describe('async actions', () => {
  var app = <App store={store} flux={mockFlux} />

  test('App before schema loaded OK', () => {
    expect(shallow(app).dive()).toMatchSnapshot()
  })

  test('App after schema loaded OK', done => {
    dispatchAndExpect(store, done, 'LOAD_SCHEMA_SUCCEEDED',
      () => loadSchemaRequested(),
      () => expect(shallow(app).dive()).toMatchSnapshot()
    )
  })
})