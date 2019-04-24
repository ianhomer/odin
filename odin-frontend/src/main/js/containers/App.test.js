import React from 'react'

import {shallow} from '../../../test/js/enzyme.js'
import sinon from 'sinon'

import App from './App'
import {LOAD_PERFORMANCE_SCHEMA_SUCCEEDED, loadPerformanceSchemaRequested} from '../actions/index.js'

import store from '../../../test/js/store'
import {dispatchAndExpect} from '../../../test/js/utils/dispatchAndExpect'

describe('async actions', () => {
  var props = {store}
  var app = <App {...props} />

  test('App before schema loaded OK', () => {
    expect(shallow(app).dive()).toMatchSnapshot()
  })

  test('App after schema loaded OK', done => {
    var shallowApp = shallow(app)
    const spyReceiveProps = sinon.spy(App.prototype, 'componentWillReceiveProps')
    expect(spyReceiveProps.calledOnce).toBe(false)
    expect(shallowApp.dive()).toMatchSnapshot()
    dispatchAndExpect(store, done, LOAD_PERFORMANCE_SCHEMA_SUCCEEDED,
      () => loadPerformanceSchemaRequested(),
      () => {
        shallowApp.setProps(Object.assign({}, props, {schema: store.getState().schema}))
        expect(shallowApp.dive()).toMatchSnapshot()
        expect(spyReceiveProps.calledOnce).toBe(true)
      }
    )
  })
})