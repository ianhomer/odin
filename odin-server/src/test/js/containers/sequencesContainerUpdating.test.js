import TIMEOUT from '../constants.js'

import React from 'react'
import SequencesContainer from 'odin/containers/sequencesContainer.js'
import { LOAD_PROJECT_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED,
  loadSchemaActions } from 'odin/actions/index.js'
import { mount } from 'enzyme'

import { dispatchAndExpect } from '../utils/dispatchAndExpect'
import { mockFlux, testProject, testSchema } from '../testData.js'
import store from '../store'

test('Sequences container renders OK', done => {
  const component =
    <SequencesContainer store={store} schema={testSchema} project={testProject} flux={mockFlux}/>

  dispatchAndExpect(store, done,
    [LOAD_PROJECT_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED],
    () => loadSchemaActions,
    () => {
    }
  )

  const mounted = mount(component)
  const row = mounted.findWhere(n => n.key() == 'aahs-a').at(1)
  expect(row).toHaveLength(1)
  expect(row).toMatchSnapshot()
  expect(row.find('.col-3 div span').at(0).text()).toBe('C A C5/h C5/8')
}, TIMEOUT)