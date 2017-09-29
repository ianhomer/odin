

import React from 'react'
import { shallow } from 'enzyme'
import sinon from 'sinon'

import App from 'odin/containers/app.js'
import { LOAD_PROJECT_SCHEMA_SUCCEEDED, loadProjectSchemaRequested } from 'odin/actions/index.js'

import { mockFlux } from '../testData.js'
import store from '../store'
import { dispatchAndExpect } from '../utils/dispatchAndExpect'

describe('async actions', () => {
  var props = {store, flux: mockFlux}
  var app = <App {...props} />

  test('App before schema loaded OK', () => {
    expect(shallow(app).dive()).toMatchSnapshot()
  })

  test('App after schema loaded OK', done => {
    var shallowApp = shallow(app)
    const spyReceiveProps = sinon.spy(App.prototype, 'componentWillReceiveProps')
    dispatchAndExpect(store, done, LOAD_PROJECT_SCHEMA_SUCCEEDED,
      () => loadProjectSchemaRequested(),
      () => {
        expect(shallowApp.dive()).toMatchSnapshot()
        expect(spyReceiveProps.calledOnce).toBe(false)
        shallowApp.setProps(Object.assign({}, props, {schema: store.getState().schema}))
        expect(shallowApp.dive()).toMatchSnapshot()
        expect(spyReceiveProps.calledOnce).toBe(true)
      }
    )
  })
})