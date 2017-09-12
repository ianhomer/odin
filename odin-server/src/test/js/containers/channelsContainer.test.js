import React from 'react'
import renderer from 'react-test-renderer'
import ChannelsContainer from 'odin/containers/channelsContainer.js'
import { Provider } from 'react-redux'
import { createStore } from 'redux'

import reducer from 'odin/reducers/index.js'
import { mockFlux, testProject, testSchema } from '../testData.js'

const store = createStore(reducer)

test('Channels container renders OK', () => {
  const component = renderer.create(
    <Provider store={store}>
      <ChannelsContainer schema={testSchema} project={testProject} flux={mockFlux}/>
    </Provider>
  )
  let tree = component.toJSON()
  expect(tree).toMatchSnapshot()
})