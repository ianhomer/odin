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

'use strict'

const React = require('react')
const PropTypes = require('prop-types')

const crud = require('../crud')

const EditEntity = require('./editEntity')

const Notation = require('./notation')
const Pattern = require('./pattern')
const Sequence = require('./sequence')
const Sequences = {
  'notation' : Notation,
  'pattern'  : Pattern,
}
const DefaultSequence = Sequence

// Rendering of generic sequence list
class SequenceList extends React.Component{
  constructor(props) {
    super(props)
    // TODO : remove schema and links from this state if redundant
    this.state = {
      schema: [], entities: [], links: []
    }

    crud.bindMe(this)
  }

  componentWillMount() {
    this.props.schema.loadClazzes(['sequence', 'pattern']).then(() => {
      this.loadFromServer()
    })
  }

  render() {
    var entities = this.state.entities.map(entity => {
      var SequenceComponent = Sequences[entity.flowName] || DefaultSequence
      return (
        // TODO - change key to simply entity.name
        <div key={'div-' + entity._links.self.href}>
          <SequenceComponent entity={entity} key={entity._links.self.href}
            schema={this.props.schema} project={this.props.project} flux={this.props.flux}
            clazz={this.props.schema.getFlowClazz(entity.flowName)}
            onDelete={this.onDelete} onUpdate={this.onUpdate}
          />
        </div>
      )
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

          {Object.keys(Sequences).map(flowName => {
            var SequenceComponent = Sequences[flowName]
            if (this.props.schema.areSchemasLoaded(['sequence', 'flow-' + flowName])) {
              var clazz = this.props.schema.getFlowClazz(flowName)
              return (
                <EditEntity key={'create-' + flowName}
                  schema={this.props.schema} project={this.props.project} flux={this.props.flux}
                  clazz={clazz} fields={SequenceComponent.defaultProps.fields}
                  onApply={this.onCreate}
                />
              )
            } else {
              return (<div key={'create-' + flowName}>{flowName} class not loaded</div>)
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
  flux: PropTypes.object.isRequired,
  schema: PropTypes.object.isRequired
}

module.exports = SequenceList