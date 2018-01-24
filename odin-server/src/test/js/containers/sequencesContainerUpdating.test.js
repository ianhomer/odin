import TIMEOUT from '../constants.js'

import React from 'react'
import SequencesContainer from 'odin/containers/SequencesContainer.js'
import {LOAD_PERFORMANCE_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED,
  loadSchemaActions} from 'odin/actions/index.js'
import {mount} from 'enzyme'

import {dispatchAndExpect} from '../utils/dispatchAndExpect'
import {testPerformance, testSchema} from '../testData.js'
import store from '../store'

describe('Sequence container validation', () => {
  test('Sequence update', done => {
    const component =
      <SequencesContainer store={store} schema={testSchema} performance={testPerformance}/>

    dispatchAndExpect(store, done,
      [LOAD_PERFORMANCE_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED],
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

  test('Sequence create', done => {
    const component =
      <SequencesContainer store={store} schema={testSchema} performance={testPerformance}/>

    dispatchAndExpect(store, done,
      [LOAD_PERFORMANCE_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED],
      () => loadSchemaActions,
      () => {
      }
    )

    const mounted = mount(component)

    debugger
    const row = mounted.findWhere(n => n.key() == 'create-notation')
    expect(row).toMatchSnapshot()

    // Fill in the new sequence form
    row.find('input.property-name').simulate('change', {target: {value: 'new-test-sequence', name: 'name'}})
    row.find('input.property-notation').simulate('change', {target: {value: 'A B C', name: 'notation'}})
    row.find('input.property-channel').simulate('change', {target: {value: '4', name: 'channel'}})
    row.find('button.btn-primary').simulate('click')

    // Verify that the new sequence is now in the redux store
    var entities = component.props.store.getState().rest.sequence.entities
    var newSequence = entities.find(entity => entity.name == 'new-test-sequence')
    expect(newSequence).toBeDefined()

    // Verify that the new sequence rendered on the page
    const newNameRow = mounted.findWhere(n => n.key() == 'new-test-sequence').at(1)
    expect(newNameRow).toMatchSnapshot()
    expect(newNameRow.find('.property-name').text()).toBe('new-test-sequence')
    expect(newNameRow.find('.property-notation').text()).toBe('A B C')
    expect(newNameRow.find('.property-channel').text()).toBe('4')

  }, TIMEOUT)

})