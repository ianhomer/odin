import TIMEOUT from '../constants.js'

import React from 'react'
import ComposerContainer from 'odin/containers/ComposerContainer.js'
import {Provider} from 'react-redux'
import {mount} from '../enzyme.js'

import {testPerformance, testSchema} from '../testData.js'
import store from '../store'

describe('Composer container', () => {
  test('Initialise', () => {
    const component = mount(
      <Provider store={store}>
        <ComposerContainer schema={testSchema} performance={testPerformance}/>
      </Provider>
    )
    expect(component).toMatchSnapshot()
  }, TIMEOUT)
})