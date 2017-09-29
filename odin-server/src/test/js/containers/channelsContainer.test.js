import React from 'react'
import ChannelsContainer from 'odin/containers/channelsContainer.js'
import {Provider} from 'react-redux'
import {mount} from 'enzyme'

import {mockFlux, testProject, testSchema} from '../testData.js'
import store from '../store'

test('Channels container renders OK', () => {
  const component = mount(
    <Provider store={store}>
      <ChannelsContainer schema={testSchema} project={testProject} flux={mockFlux}/>
    </Provider>
  )
  expect(component).toMatchSnapshot()
  expect(component.find('button')).toHaveLength(6)
  component.find('button').at(0).simulate('click')
  expect(component.find('button')).toHaveLength(5)
  component.find('button').at(0).simulate('click')
  expect(component.find('button')).toHaveLength(4)
})