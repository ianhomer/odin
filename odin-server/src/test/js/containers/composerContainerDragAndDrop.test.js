import React, {Component} from 'react'
import TestBackend from 'react-dnd-test-backend'
import {DragDropContext} from 'react-dnd'
import TestUtils from 'react-dom/test-utils'

import ComposerContainer from 'odin/containers/composerContainer.js'
//import Layer from 'odin/components/composer/layer.js'
import {Provider} from 'react-redux'

import {testProject, testSchema} from '../testData.js'
import store from '../store'


function wrapInTestContext(DecoratedComponent) {
  return DragDropContext(TestBackend)(
    class TestContextContainer extends Component {
      render() {
        return <DecoratedComponent {...this.props} />
      }
    }
  )
}

describe('Composer container drag and drop', () => {
  test('Initialise', () => {
    const ProviderInContext = wrapInTestContext(Provider)
    const root = TestUtils.renderIntoDocument(
      <ProviderInContext store={store}>
        <ComposerContainer schema={testSchema} project={testProject}/>
      </ProviderInContext>
    )
    //const backend = root.getManager().getBackend()
    let cardA = TestUtils.scryRenderedDOMComponentsWithClass(root, 'card').find(card => card.title == 'a')
    expect(cardA).toMatchSnapshot()
    expect(cardA.getAttribute('draggable')).toBeTruthy()

    //let cardAComponent = TestUtils.scryRenderedComponentsWithType(root, Layer).find(card => card.props.entity.name == 'a')
    //console.log(cardAComponent)
    //backend.simulateBeginDrag([cardAComponent.getHandlerId()]);
    //expect(cardAComponent).toMatchSnapshot()
  })
})