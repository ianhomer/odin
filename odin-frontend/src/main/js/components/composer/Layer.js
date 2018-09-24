// Copyright (c) 2017 The Odin Authors. All Rights Reserved
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

import React from 'react'
import PropTypes from 'prop-types'

import {ItemTypes} from '../../constants'
import {DragSource, DropTarget} from 'react-dnd'

import SequenceInLayer from './SequenceInLayer'

/**
 * Implements the drag source contract.
 */
const dragSource = {
  beginDrag(props) {
    return {
      onChange: props.onChange,
      entity: props.entity,
      parent: props.parent,
      type: ItemTypes.LAYER
    }
  },

  endDrag(props, monitor) {
    if (monitor.didDrop()) {
      var action = monitor.getDropResult().action
      if (action === 'remove') {
        props.onDelete(props.entity)
      } else if (action === 'add') {
        props.onAddLayer(monitor.getDropResult().entity, props.entity, props.onChange)
      } else if (action === 'move') {
        props.onMoveLayer(monitor.getDropResult().entity, props.parent, props.entity, props.onChange)
      } else {
        console.error('Action ' + action + ' has not been implemented for layers : result = ' +
          JSON.stringify(monitor.getDropResult()))
      }
    }
  }
}

const dropTarget = {
  drop(props) {
    return {
      action: 'move',
      entity: props.entity,
      sequences: props.sequences,
      isOverCurrent: props.isOverCurrent
    }
  },

  canDrop(props, monitor) {
    var isOverCurrent = monitor.isOver({shallow: true})
    if (monitor.getItem().type === ItemTypes.SEQUENCE) {
      // can drop sequence
      for (var i =0 ; i < props.sequences.length ; i++) {
        // Sequence is already in layer
        if (props.sequences[i].name === monitor.getItem().entity.name) {
          return false
        }
      }
    } else {
      // can drop layer
      if (props.entity.name === monitor.getItem().entity.name) {
        return false
      }
    }
    return isOverCurrent
  }
}

function collectDrag(connect, monitor) {
  return {
    connectDragSource: connect.dragSource(),
    isDragging: monitor.isDragging()
  }
}

function collectDrop(connect, monitor) {
  return {
    connectDropTarget: connect.dropTarget(),
    isOver: monitor.isOver(),
    isOverCurrent: monitor.isOver({shallow: true}),
    canDrop: monitor.canDrop()
  }
}

class Layer extends React.Component{
  constructor(props) {
    super(props)

    this.handleChange = this.handleChange.bind(this)
  }

  handleChange(props) {
    this.props.onRemoveLayer(props.entity, this.props.entity)
  }

  render() {
    var sequences = this.props.sequences

    // Lookup the sequences that are in this layer and pass this array into the layer component
    var sequencesInLayer = []
    for (var sequenceName in sequences) {
      var sequence = sequences[sequenceName]
      for (var j = 0 ; j < sequence.layers.length ; j++) {
        var layerName = sequence.layers[j]
        if (layerName === this.props.entity.name) {
          // Push a sequence object onto the array with enough information to handle change
          sequencesInLayer.push(sequence)
        }
      }
    }

    var sequenceNames = sequencesInLayer.map(entity =>
      <SequenceInLayer key={entity.name + '-' + this.props.entity._links.self.href}
        entity={entity}
        onChange={this.handleChange}/>
    )


    const {connectDropTarget, connectDragSource, isOverCurrent, canDrop} = this.props

    var style = {}
    if (isOverCurrent && canDrop) {
      style['backgroundColor'] = 'darkgreen'
    }

    if (connectDragSource && connectDropTarget) {
      return connectDragSource(connectDropTarget(
        <div className="layer card" style={style} title={this.props.entity.name}>
          {this.props.entity.name}
          {this.props.children}
          <div className="sequences">{sequenceNames}</div>
        </div>
      ))
    } else {
      // TODO : We can probably remove this block now, since nesting mostly OK.
      return (
        <div className="layer card" style={style}>
          ! {this.props.entity.name} !
          <div className="sequences">{sequenceNames}</div>
        </div>
      )
    }
  }
}

Layer.propTypes = {
  children: PropTypes.node,
  entity: PropTypes.object.isRequired,
  layers: PropTypes.object.isRequired,
  onDelete: PropTypes.func.isRequired,
  onRemoveLayer: PropTypes.func.isRequired,
  parent: PropTypes.object,
  sequences: PropTypes.object.isRequired,

  // Injected by React DnD:
  isOverCurrent: PropTypes.bool.isRequired,
  canDrop: PropTypes.bool.isRequired,
  connectDropTarget: PropTypes.func.isRequired,
  connectDragSource: PropTypes.func.isRequired
}

export default DragSource(ItemTypes.LAYER, dragSource, collectDrag)(
  DropTarget([ItemTypes.SEQUENCE, ItemTypes.LAYER], dropTarget, collectDrop)(Layer))