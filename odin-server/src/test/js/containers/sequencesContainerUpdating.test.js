import TIMEOUT from '../constants.js'

import React from 'react'
import SequencesContainer from 'odin/containers/sequencesContainer.js'
import {LOAD_PROJECT_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED,
  loadSchemaActions} from 'odin/actions/index.js'
import {mount} from 'enzyme'

import {dispatchAndExpect} from '../utils/dispatchAndExpect'
import {testProject, testSchema} from '../testData.js'
import store from '../store'

test('Sequences container renders OK', done => {
  const component =
    <SequencesContainer store={store} schema={testSchema} project={testProject}/>

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

  expect(row.find('.property-name').text()).toBe('aahs-a')
  expect(row.find('.property-notation').text()).toBe('C A C5/h C5/8')
  expect(row.find('.property-channel').text()).toBe('4')
  row.simulate('click')
  expect(row).toMatchSnapshot()
  row.find('input.property-notation').simulate('change', {target: {value: 'C D E', name: 'notation'}})
  row.find('input.property-name').simulate('change', {target: {value: 'aahs-changed', name: 'name'}})
  row.find('input.property-channel').simulate('change', {target: {value: '5', name: 'channel'}})
  expect(row).toMatchSnapshot()
  row.find('input.property-notation').simulate('keypress', {key: 'Enter'})
  expect(mounted.findWhere(n => n.key() == 'aahs-a')).toHaveLength(0)

  const changeNameRow = mounted.findWhere(n => n.key() == 'aahs-changed').at(1)
  expect(changeNameRow).toMatchSnapshot()
  expect(changeNameRow.find('.property-notation').text()).toBe('C D E')
  expect(changeNameRow.find('.property-name').text()).toBe('aahs-changed')
  expect(changeNameRow.find('.property-channel').text()).toBe('5')
}, TIMEOUT)