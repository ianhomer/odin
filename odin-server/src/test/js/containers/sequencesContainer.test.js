import React from 'react'
import SequencesContainer from 'odin/containers/sequencesContainer.js'
import { LOAD_PROFILE_SCHEMA_SUCCEEDED, loadSchemaActions } from 'odin/actions/index.js'
import { Provider } from 'react-redux'
import { mount } from 'enzyme'

import { dispatchAndExpect } from '../utils/dispatchAndExpect'
import { mockFlux, testProject, testSchema } from '../testData.js'
import store from '../store'

test('Sequences container renders OK', done => {
  const component = mount(
    <Provider store={store}>
      <SequencesContainer schema={testSchema} project={testProject} flux={mockFlux}/>
    </Provider>
  )
  dispatchAndExpect(store, done, LOAD_PROFILE_SCHEMA_SUCCEEDED,
    () => loadSchemaActions,
    () => {
      expect(component).toMatchSnapshot()
    }
  )
})