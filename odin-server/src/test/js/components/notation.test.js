import React from 'react';
//import Notation from 'components/notation.js';
import renderer from 'react-test-renderer';

test('Notation renders OK', () => {
  var entity = {
    'name' : 'test-name',
    'flowName' : 'notation',
    'channel' : 1,
    'offset' : 0,
    'length' : -1,
    'tick' : {
      'timeUnit' : 'BEAT',
      'numerator' : 1,
      'denominator' : 1
    },
    'properties' : {
      'notation' : 'A/q G/8 A/q E',
      'format' : 'natural'
    },
    'layers' : [
      'a', 'c'
    ]
  };
// Re-enable test when Notation decoupled from crud
//  const component = renderer.create(
//    <Notation entity={entity} key='key'
//      schema={this.props.schema} project={this.props.project}
//    />
//  );
//  let tree = component.toJSON();
//  expect(tree).toMatchSnapshot();
});