import TIMEOUT from '../constants.js'

import React from 'react'
import SequencesContainer from './SequencesContainer.js'
import {LOAD_PERFORMANCE_SCHEMA_SUCCEEDED, LOAD_PROFILE_SCHEMA_SUCCEEDED,
  loadSchemaActions} from '../actions/index.js'
import {mount} from '../../../test/js/enzyme.js'
import {mountToJson} from 'enzyme-to-json'

import {dispatchAndExpect} from '../../../test/js/utils/dispatchAndExpect'
import {testPerformance, testSchema} from '../../../test/js/testData.js'
import store from '../../../test/js/store'
import '../../../test/js/global-document'

describe('Sequences container', () => {
  const component =
    <SequencesContainer store={store} schema={testSchema} performance={testPerformance}/>

  test('Sequences container initialise', () => {
    expect(mountToJson(mount(component),  {mode: 'deep'})).toMatchSnapshot()
  }, TIMEOUT)

  test('Sequences container row deletion', () => {
    // Test deleting a sequence
    const mounted = mount(component)
    expect(mounted.find('button')).toHaveLength(26)
    mounted.find('button').at(0).simulate('click')
    expect(mounted.find('button')).toHaveLength(25)
  }, TIMEOUT)
})