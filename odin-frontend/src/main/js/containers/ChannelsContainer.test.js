import React from 'react'
import ChannelsContainer from './ChannelsContainer.js'
import {Provider} from 'react-redux'
import {mount} from '../../../test/js/enzyme.js'

import {testPerformance, testSchema} from '../../../test/js/testData.js'
import store from '../../../test/js/store'

describe('Channels container', () => {

  test('Initialise', () => {
    const component = mount(
      <Provider store={store}>
        <ChannelsContainer schema={testSchema} performance={testPerformance}/>
      </Provider>
    )
    expect(component).toMatchSnapshot()
    expect(component.find('button')).toHaveLength(6)
    component.find('button').at(0).simulate('click')
    expect(component.find('button')).toHaveLength(5)
    component.find('button').at(0).simulate('click')
    expect(component.find('button')).toHaveLength(4)
  })
})