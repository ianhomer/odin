import React, {Component} from 'react'
import TestBackend from 'react-dnd-test-backend'
import { DndProvider } from "react-dnd";
import TestUtils from 'react-dom/test-utils'

import ComposerContainer from './ComposerContainer.js'
import Layer from '../components/composer/Layer.js'
import {Provider} from 'react-redux'

import {testPerformance, testSchema} from '../../../test/js/testData.js'
import store from '../../../test/js/store'
import '../../../test/js/global-document'

import toJson from 'enzyme-to-json'

function wrapInTestContext(DecoratedComponent) {
  return (props) => (
    <DndProvider backend={TestBackend}>
      <DecoratedComponent { ...props} />
    </DndProvider>
  )
}

describe('Composer container drag and drop', () => {
  test.skip('Initialise', () => {
    const ProviderInContext = wrapInTestContext(Provider)
    const root = TestUtils.renderIntoDocument(
      <ProviderInContext store={store}>
        <ComposerContainer schema={testSchema} performance={testPerformance}/>
      </ProviderInContext>
    )
    //const backend = root.getManager().getBackend()

    expect(toJson(root)).toMatchSnapshot()
    let cardA = TestUtils.scryRenderedDOMComponentsWithClass(root, 'card').find(card => card.title === 'a')
    expect(cardA).toBeTruthy()
    expect(cardA).toMatchSnapshot()
    expect(cardA.getAttribute('draggable')).toBeTruthy()

    let sourceComponent = TestUtils.scryRenderedComponentsWithType(root, Layer).find(card => card.props.entity.name === 'a')
    let targetComponent = TestUtils.scryRenderedComponentsWithType(root, Layer).find(card => card.props.entity.name === 'b')
    let sourceId = sourceComponent.handlerId
    let targetId = targetComponent.getDecoratedComponentInstance().getHandlerId()
    expect(sourceId).toBeTruthy()
    expect(targetId).toBeTruthy()
    // TODO : Complete test coverage of drag and drop
    // currently the following throws Invariant Violation: Expected sourceIds to be registered.
    // see actionsLayers.test.js for examples of the dispatches that we'd expect to happen
    //backend.simulateBeginDrag([sourceId])
    //backend.simulateHover([targetId])
    //backend.simulateDrop()
    //backend.simulateEndDrag()
  })
})
