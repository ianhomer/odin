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
import EditEntity from './EditEntity'
import Notation from './Notation'
import Pattern from './Pattern'
import Sequence from './Sequence'

const Sequences = {
  'notation': Notation,
  'pattern': Pattern
}
const DefaultSequence = Sequence

// Rendering of generic sequence list
class SequenceList extends React.Component{
  constructor(props) {
    super(props)
  }

  getExtraArguments(type, isEditEntry = false) {
    var optionalArguments = {}
    if (type === 'notation') {
      optionalArguments.onFetchComposition = this.props.onFetchComposition
      if (isEditEntry && this.props.collection.newEntities && this.props.collection.newEntities.notation) {
        optionalArguments.entity = this.props.collection.newEntities.notation
      }
    }
    return optionalArguments
  }

  render() {
    var entities = this.props.collection.entities.map(entity => {
      var SequenceComponent = Sequences[entity.type] || DefaultSequence
      if (this.props.schema.areSchemasLoaded(['sequence', 'flow-' + entity.type])) {
        return (
          <div key={entity.name}>
            <SequenceComponent entity={entity} key={entity.name}
              schema={this.props.schema} project={this.props.project}
              clazz={this.props.schema.getFlowClazz(entity.type)}
              onDelete={this.props.onDelete} onUpdate={this.props.onUpdate}
              {...this.getExtraArguments(entity.type)}
            />
          </div>
        )
      } else {
        return <div key={entity.name}>{entity.type} or sequence class not loaded</div>
      }
    })

    return (
      // View sequence list.
      <div>
        <div className="container">
          <div className="row">
            <div className="col-1">Name</div>
            <div className="col-3">&nbsp;</div>
            <div className="col-2">Tick</div>
            <div className="col-1">Offset</div>
            <div className="col-1">Length</div>
            <div className="col-1">Channel</div>
            <div className="col-2">Flow Name</div>
          </div>

          {entities}

          {/* Render edit entities for each sequence type */}

          <hr/>

          {Object.keys(Sequences).map(type => {
            var SequenceComponent = Sequences[type]
            if (this.props.schema.areSchemasLoaded(['sequence', 'flow-' + type])) {
              var clazz = this.props.schema.getFlowClazz(type)
              return (
                <EditEntity key={'create-' + type}
                  schema={this.props.schema} project={this.props.project}
                  clazz={clazz} fields={SequenceComponent.defaultProps.fields}
                  onApply={this.props.onCreate}
                  {...this.getExtraArguments(type, true)}
                />
              )
            } else {
              return <div key={'create-' + type}>{type} or sequence class not loaded</div>
            }
          })}

        </div>
      </div>
    )
  }
}

SequenceList.defaultProps = {
  path: 'sequence'
}

SequenceList.propTypes = {
  collection: PropTypes.object.isRequired,
  onCreate: PropTypes.func.isRequired,
  onFetchComposition: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
  onUpdate: PropTypes.func.isRequired,
  project: PropTypes.object.isRequired,
  schema: PropTypes.object.isRequired
}

module.exports = SequenceList