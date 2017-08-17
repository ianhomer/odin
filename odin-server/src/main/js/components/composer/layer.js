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
import { DragSource, DropTarget } from 'react-dnd';

const crud = require('../../crud');

const SequenceInLayer = require('./sequenceInLayer');

/**
 * Implements the drag source contract.
 */
const dragSource = {
  beginDrag(props) {
    return {
      onChange : props.onChange,
      entity : props.entity
    };
  },

  endDrag(props, monitor) {
    if (monitor.didDrop()) {
      var action = monitor.getDropResult().action;
      if (action == 'remove') {
        props.onDelete(props.entity);
      } else if (action == 'add') {
        props.onAddLayer(monitor.getDropResult().entity, props.entity, props.onChange);
      } else {
        console.error('Only remove and add action have been implemented for layers : result = ' +
          JSON.stringify(monitor.getDropResult()));
      }
    }
  }
};

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

function collectDrag(connect, monitor) {
  return {
    connectDragSource: connect.dragSource(),
    isDragging: monitor.isDragging()
  };
}

function collectDrop(connect, monitor) {
  return {
    connectDropTarget: connect.dropTarget(),
    isOver: monitor.isOver()
  };
}

const propTypes = {
  entity: PropTypes.object.isRequired,
  onChange: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
  sequences: PropTypes.object.isRequired,
  layers: PropTypes.object.isRequired,
  children: PropTypes.node
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
    var sequences = this.props.sequences;

    // Lookup the sequences that are in this layer and pass this array into the layer component
    var sequencesInLayer = [];
    for (var sequenceName in sequences) {
      var sequence = sequences[sequenceName];
      for (var j = 0 ; j < sequence.layers.length ; j++) {
        var layerName = sequence.layers[j];
        if (layerName == this.props.entity.name) {
          // Push a sequence object onto the array with enough information to handle change
          sequencesInLayer.push({
            name : sequence.name,
            index : j,
            href : sequence._links.self.href
          });
        }
      }
    }

    var sequenceNames = sequencesInLayer.map(entry =>
      <SequenceInLayer key={entry.name + '-' + this.props.entity._links.self.href}
        name={entry.name} layerIndex={entry.index} href={entry.href}
        onChange={this.handleChange}/>
    );

    const { connectDropTarget, connectDragSource } = this.props;
    if (connectDragSource && connectDropTarget) {
      return connectDragSource(connectDropTarget(
        <div className="layer card">
          {this.props.entity.name}
          {this.props.children}
          <div className="sequences">{sequenceNames}</div>
        </div>
      ));
    } else {
      return (
        <div className="layer card">
          ! {this.props.entity.name} !
          <div className="sequences">{sequenceNames}</div>
        </div>
      );
    }
  }
}

Layer.propTypes = propTypes;

module.exports = DragSource(ItemTypes.LAYER, dragSource, collectDrag)(
  DropTarget([ ItemTypes.SEQUENCE, ItemTypes.LAYER], dropTarget, collectDrop)(Layer));