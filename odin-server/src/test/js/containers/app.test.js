import React from 'react'
import { shallow } from 'enzyme'
import toJson from 'enzyme-to-json'

import App from 'odin/containers/app.js'

import { mockFlux } from '../testData.js'
import store from '../store'

test('App renders OK', () => {
  const app = shallow(<App store={store} flux={mockFlux} />)
  expect(toJson(app)).toMatchSnapshot()
})