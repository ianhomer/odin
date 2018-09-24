import TIMEOUT from '../constants.js'

import React from 'react'
import EnvironmentContainer from './EnvironmentContainer.js'
import {Provider} from 'react-redux'
import {mount} from '../../../test/js/enzyme.js'

import store from '../../../test/js/store'
import '../../../test/js/global-document'

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