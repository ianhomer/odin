import React from 'react'
import Tick from './Tick'
import renderer from 'react-test-renderer'

test('Tick renders OK', () => {
  const component = renderer.create(
    <Tick numerator={1} denominator={2}/>
  )
  let tree = component.toJSON()
  expect(tree).toMatchSnapshot()
})