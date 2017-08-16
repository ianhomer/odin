// Copyright (c) 2017 Ian Homer. All Rights Reserved
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

'use strict';

const React = require('react');
import PropTypes from 'prop-types';

import { ItemTypes } from '../../constants';
import { DropTarget } from 'react-dnd';

const crud = require('../../crud');

const SequenceInLayer = require('./sequenceInLayer');

const dropTarget = {
  drop(props) {
    return {
      action : 'add',
      entity : props.entity,
      sequences : props.sequences
    };
  },

  canDrop(props, monitor) {
    for (var i =0 ; i < props.sequences.length ; i++) {
      // Sequence is already in layer
      if (props.sequences[i].name == monitor.getItem().entity.name) {
        return false;
      }
    }
    return true;
  }
};

function collect(connect, monitor) {
  return {
    connectDropTarget: connect.dropTarget(),
    isOver: monitor.isOver()
  };
}

const propTypes = {
  entity: PropTypes.object.isRequired,
  onChange: PropTypes.func.isRequired,
  sequences: PropTypes.array.isRequired
};

class Layer extends React.Component{
  constructor(props) {
    super(props);

    this.handleChange = this.handleChange.bind(this);
    this.onPatch = crud.onPatch.bind(this);
  }

  handleChange(props) {
    var layerPath = '/layers/' + props.layerIndex;
    this.onPatch(props.href,
      [
        // Test layer at given index in array is the as expected on server
        { op: 'test', path: layerPath, value: this.props.entity.name },
        // ... then remove it.
        { op: 'remove', path: layerPath  }
      ]
    );
    this.props.onChange();
  }

  render() {
    var sequenceNames = this.props.sequences.map(entry =>
      <SequenceInLayer key={'layer-' + this.props.entity.name + '-' + entry.name}
        name={entry.name} layerIndex={entry.index} href={entry.href}
        onChange={this.handleChange}/>
    );
    const { connectDropTarget } = this.props;
    return connectDropTarget(
      <div className="layer card">
        {this.props.entity.name}
        <div className="sequences">{sequenceNames}</div>
      </div>
    );
  }
}

module.exports = DropTarget(ItemTypes.SEQUENCE, dropTarget, collect)(Layer);