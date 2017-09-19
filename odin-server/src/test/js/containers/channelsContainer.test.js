import React from 'react'
import renderer from 'react-test-renderer'
import ChannelsContainer from 'odin/containers/channelsContainer.js'

import thunkMiddleware from 'redux-thunk'
import { Provider } from 'react-redux'
import { createStore, applyMiddleware } from 'redux'
import createSagaMiddleware from 'redux-saga'

import reducer from 'odin/reducers/index.js'
import { mockFlux, testProject, testSchema } from '../testData.js'
import backend from '../backend/mock.js'

const sagaMiddleware = createSagaMiddleware()
const store = createStore(
  reducer,
  applyMiddleware(
    thunkMiddleware,
    sagaMiddleware
  )
)

sagaMiddleware.run(backend)

test('Channels container renders OK', () => {
  const component = renderer.create(
    <Provider store={store}>
      <ChannelsContainer schema={testSchema} project={testProject} flux={mockFlux}/>
    </Provider>
  )
  let tree = component.toJSON()
  expect(tree).toMatchSnapshot()
})