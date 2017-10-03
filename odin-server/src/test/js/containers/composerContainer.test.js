import React from 'react'
import ComposerContainer from 'odin/containers/composerContainer.js'
import {Provider} from 'react-redux'
import {mount} from 'enzyme'

import {testProject, testSchema} from '../testData.js'
import store from '../store'

describe('Sequences container', () => {
  test('Initialise', () => {
    const component = mount(
      <Provider store={store}>
        <ComposerContainer schema={testSchema} project={testProject}/>
      </Provider>
    )
    expect(component).toMatchSnapshot()
  })
})