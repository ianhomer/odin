import React from 'react'
import ChannelsContainer from 'odin/containers/ChannelsContainer.js'
import {Provider} from 'react-redux'
import {mount} from '../enzyme.js'

import {testPerformance, testSchema} from '../testData.js'
import store from '../store'

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