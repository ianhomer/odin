'use strict'

import React from 'react'
import { shallow } from 'enzyme'

import App from 'odin/containers/app.js'
import { LOAD_PROJECT_SCHEMA_SUCCEEDED, loadProjectSchemaRequested } from 'odin/actions/index.js'


import { mockFlux } from '../testData.js'
import store from '../store'
import { dispatchAndExpect } from '../utils/dispatchAndExpect'

describe('async actions', () => {
  var app = <App store={store} flux={mockFlux} />

  test('App before schema loaded OK', () => {
    expect(shallow(app).dive()).toMatchSnapshot()
  })

  test('App after schema loaded OK', done => {
    dispatchAndExpect(store, done, LOAD_PROJECT_SCHEMA_SUCCEEDED,
      () => loadProjectSchemaRequested(),
      () => expect(shallow(app).dive()).toMatchSnapshot()
    )
  })
})