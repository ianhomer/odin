import React from 'react'
import SequencesContainer from 'odin/containers/sequencesContainer.js'
import { Provider } from 'react-redux'
import { mount } from 'enzyme'

import { mockFlux, testProject, testSchema } from '../testData.js'
import store from '../store'

test('Sequences container renders OK', () => {
  const component = mount(
    <Provider store={store}>
      <SequencesContainer schema={testSchema} project={testProject} flux={mockFlux}/>
    </Provider>
  )
  expect(component).toMatchSnapshot()
})