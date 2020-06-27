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
import {DragDropContext} from 'react-dnd'
import { HTML5Backend } from 'react-dnd-html5-backend'

import LayerList from './LayerList'
import NotationCard from './NotationCard'
import PatternCard from './PatternCard'
import SequenceCard from './SequenceCard'
const Sequences = {
  'notation': NotationCard,
  'pattern': PatternCard
}
const DefaultSequence = SequenceCard

// Rendering of composer
class Composer extends React.Component{
  getExtraArguments(type) {
    var optionalArguments = {}
    if (type === 'notation') {
      optionalArguments.onFetchComposition = this.props.onFetchComposition
    }
    return optionalArguments
  }

  render() {
    var entities = this.props.sequences.entities.map(entity => {
      var SequenceComponent = Sequences[entity.type] || DefaultSequence
      return (
        <div key={'div-' + entity._links.self.href}>
          <SequenceComponent entity={entity} key={entity._links.self.href}
            schema={this.props.schema} performance={this.props.performance}
            onAddLayer={this.props.onAddLayer}
            {...this.getExtraArguments(entity.type)}
          />
        </div>
      )
    })

    return (
      // Display layer list and sequence cards
      <div>
        {this.props.layers.entities.length > 0 &&
          <LayerList
            schema={this.props.schema} performance={this.props.performance}
            layers={this.props.layers}
            sequences={this.props.sequences}
            onCreate={this.props.onCreate}
            onDelete={this.props.onDelete}
            onAddLayer={this.props.onAddLayer}
            onMoveLayer={this.props.onMoveLayer}
            onRemoveLayer={this.props.onRemoveLayer}
          />
        }
        <div className="break">&nbsp;</div>
        <div>{entities}</div>
      </div>
    )
  }
}

Composer.defaultProps = {
  path: 'sequence'
}

Composer.propTypes = {
  layers: PropTypes.object.isRequired,
  onCreate: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
  onAddLayer: PropTypes.func.isRequired,
  onRemoveLayer: PropTypes.func.isRequired,
  onMoveLayer: PropTypes.func.isRequired,
  onFetchComposition: PropTypes.func.isRequired,
  performance: PropTypes.object.isRequired,
  schema: PropTypes.object.isRequired,
  sequences: PropTypes.object.isRequired
}

export default DragDropContext(HTML5Backend)(Composer)
