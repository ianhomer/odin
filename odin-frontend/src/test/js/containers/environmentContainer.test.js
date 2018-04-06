import TIMEOUT from '../constants.js'

import React from 'react'
import EnvironmentContainer from 'odin/containers/EnvironmentContainer.js'
import {Provider} from 'react-redux'
import {mount} from '../enzyme.js'

import store from '../store'

describe('Environment container', () => {
  test('Initialise', () => {
    const component = mount(
      <Provider store={store}>
        <EnvironmentContainer/>
      </Provider>
    )
    expect(component).toMatchSnapshot()
  }, TIMEOUT)
})