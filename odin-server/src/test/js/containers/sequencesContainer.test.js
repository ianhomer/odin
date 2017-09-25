import React from 'react'
import SequencesContainer from 'odin/containers/sequencesContainer.js'
import { LOAD_PROJECT_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED,
  loadSchemaActions } from 'odin/actions/index.js'
import { mount } from 'enzyme'
import { mountToDeepJson } from 'enzyme-to-json'

import { dispatchAndExpect } from '../utils/dispatchAndExpect'
import { mockFlux, testProject, testSchema } from '../testData.js'
import store from '../store'

test('Sequences container renders OK', done => {
  const component =
    <SequencesContainer store={store} schema={testSchema} project={testProject} flux={mockFlux}/>
  expect(mountToDeepJson(mount(component))).toMatchSnapshot()

  dispatchAndExpect(store, done,
    [LOAD_PROJECT_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED],
    () => loadSchemaActions,
    () => {
      expect(mountToDeepJson(mount(component))).toMatchSnapshot()
    }
  )

  const mounted = mount(component)
  expect(mounted.find('button')).toHaveLength(24)
  mounted.find('button').at(0).simulate('click')
  expect(mounted.find('button')).toHaveLength(23)
})