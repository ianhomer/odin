// Copyright (c) 2017 the original author or authors. All Rights Reserved
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



const React = require('react')
import PropTypes from 'prop-types'

import { DragSource } from 'react-dnd'
import { ItemTypes } from '../../constants.js'

const CardLayers = require('./cardLayers')

/**
 * Implements the drag source contract.
 */
const dropSource = {
  beginDrag(props) {
    return {
      onChange: props.onChange,
      entity: props.entity,
      type: ItemTypes.SEQUENCE
    }
  },

  endDrag(props, monitor) {
    if (monitor.didDrop()) {
      props.onAddLayer(props.entity, monitor.getDropResult().entity)
    }
  }
}

/**
 * Specifies the props to inject into your component.
 */
function collect(connect, monitor) {
  return {
    connectDragSource: connect.dragSource(),
    isDragging: monitor.isDragging()
  }
}

const propTypes = {
  entity: PropTypes.object.isRequired,
  onAddLayer: PropTypes.func.isRequired,
  // Injected by React DnD:
  isDragging: PropTypes.bool.isRequired,
  connectDragSource: PropTypes.func.isRequired
}

// Sequence card.
class SequenceCard extends React.Component{
  constructor(props) {
    super(props)

    this.state = {
      entity: this.props.entity
    }
  }

  render() {
    const { isDragging, connectDragSource } = this.props
    return connectDragSource(
      // View card
      <div className="sequence card" style={{ opacity: isDragging ? 0.5 : 1 }}>
        <header>{this.state.entity.name}</header>
        <div className="content">{this.state.entity.bits}</div>
        <footer><CardLayers entity={this.state.entity}/></footer>
      </div>
    )
  }
}

SequenceCard.propTypes = propTypes

module.exports = DragSource(ItemTypes.SEQUENCE, dropSource, collect)(SequenceCard)