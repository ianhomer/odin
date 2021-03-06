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
import {DragSource} from 'react-dnd'
import {ItemTypes} from '../../constants.js'

/**
 * Implements the drag source contract.
 */
const cardSource = {
  beginDrag(props) {
    return {
      entity: props.entity
    }
  },

  endDrag(props, monitor) {
    if (monitor.didDrop()) {
      props.onChange(props)
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
  onChange: PropTypes.func.isRequired,
  // Injected by React DnD:
  isDragging: PropTypes.bool.isRequired,
  connectDragSource: PropTypes.func.isRequired
}

// Sequence in layer.
class SequenceInLayer extends React.Component{
  render() {
    const {isDragging, connectDragSource, entity} = this.props
    return connectDragSource(
      // View card
      <div className="sequenceInLayer" style={{opacity: isDragging ? 0.5 : 1}}>
        {entity.name}
      </div>
    )
  }
}

SequenceInLayer.propTypes = propTypes

// Export the wrapped component:
export default DragSource(ItemTypes.SEQUENCE_IN_LAYER, cardSource, collect)(SequenceInLayer)