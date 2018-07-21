import TIMEOUT from '../constants.js'

import React from 'react'
import ComposerContainer from './ComposerContainer.js'
import {Provider} from 'react-redux'
import {mount} from '../../../test/js/enzyme.js'

import {testPerformance, testSchema} from '../../../test/js/testData.js'
import store from '../../../test/js/store'

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