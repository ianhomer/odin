import React from 'react'

import {shallow} from '../enzyme.js'
import sinon from 'sinon'

import App from 'odin/containers/App.js'
import {LOAD_PERFORMANCE_SCHEMA_SUCCEEDED, loadPerformanceSchemaRequested} from 'odin/actions/index.js'

import store from '../store'
import {dispatchAndExpect} from '../utils/dispatchAndExpect'

describe('async actions', () => {
  var props = {store}
  var app = <App {...props} />

  test('App before schema loaded OK', () => {
    expect(shallow(app).dive()).toMatchSnapshot()
  })

  test('App after schema loaded OK', done => {
    var shallowApp = shallow(app)
    const spyReceiveProps = sinon.spy(App.prototype, 'componentWillReceiveProps')
    dispatchAndExpect(store, done, LOAD_PERFORMANCE_SCHEMA_SUCCEEDED,
      () => loadPerformanceSchemaRequested(),
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