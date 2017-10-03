import React, { Component } from 'react'
import TestBackend from 'react-dnd-test-backend'
import { DragDropContext } from 'react-dnd'
import TestUtils from 'react-dom/test-utils'

import ComposerContainer from 'odin/containers/composerContainer.js'
import {Provider} from 'react-redux'
import {mount} from 'enzyme'

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
    const ProviderInContext = wrapInTestContext(Provider);
    const root = TestUtils.renderIntoDocument(
      <ProviderInContext store={store}>
        <ComposerContainer schema={testSchema} project={testProject}/>
      </ProviderInContext>
    )
    const backend = root.getManager().getBackend()
    console.log(root)
    let div = TestUtils.scryRenderedDOMComponentsWithTag(root, 'div')
    expect(div[0]).toMatchSnapshot()
  })
})