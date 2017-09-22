import React from 'react'
import SequencesContainer from 'odin/containers/sequencesContainer.js'
import { LOAD_PROJECT_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED,
  loadSchemaActions } from 'odin/actions/index.js'
import { shallow } from 'enzyme'

import { dispatchAndExpect } from '../utils/dispatchAndExpect'
import { mockFlux, testProject, testSchema } from '../testData.js'
import store from '../store'

test('Sequences container renders OK', done => {
  const component =
    <SequencesContainer store={store} schema={testSchema} project={testProject} flux={mockFlux}/>
  expect(shallow(component).dive()).toMatchSnapshot()

  dispatchAndExpect(store, done,
    [LOAD_PROJECT_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED],
    () => loadSchemaActions,
    () => {
      expect(shallow(component).dive()).toMatchSnapshot()
    }
  )
})