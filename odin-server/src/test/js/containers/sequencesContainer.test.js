import React from 'react'
import SequencesContainer from 'odin/containers/sequencesContainer.js'
import { LOAD_PROJECT_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED,
  loadSchemaActions } from 'odin/actions/index.js'
import { mount } from 'enzyme'
import { mountToDeepJson } from 'enzyme-to-json'

import { dispatchAndExpect } from '../utils/dispatchAndExpect'
import { mockFlux, testProject, testSchema } from '../testData.js'
import store from '../store'

describe('Sequences container', () => {
  const component =
    <SequencesContainer store={store} schema={testSchema} project={testProject} flux={mockFlux}/>

  test('Sequences container initialise', () => {
    expect(mountToDeepJson(mount(component))).toMatchSnapshot()
  })

  test('Sequences container load', done => {
    dispatchAndExpect(store, done,
      [LOAD_PROJECT_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED],
      () => loadSchemaActions,
      () => {
        expect(mountToDeepJson(mount(component))).toMatchSnapshot()
      }
    )
  })

  test('Sequences container row deletion', () => {
    // Test deleting a sequence
    const mounted = mount(component)
    expect(mounted.find('button')).toHaveLength(24)
    mounted.find('button').at(0).simulate('click')
    expect(mounted.find('button')).toHaveLength(23)
  })
})