import React from 'react'
import { shallow } from 'enzyme'
import toJson from 'enzyme-to-json'

import App from 'odin/containers/app.js'
import { loadSchemaRequested } from 'odin/actions/index.js'

import { mockFlux } from '../testData.js'
import store from '../store'

test('App renders OK', () => {
  store.dispatch(loadSchemaRequested())
  const app = shallow(<App store={store} flux={mockFlux} />).dive()
  expect(toJson(app)).toMatchSnapshot()
})