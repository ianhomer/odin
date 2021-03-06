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
import Tick from './Tick'

class Sequence extends React.Component {
  constructor(props) {
    super(props)

    this.state = {editing: null}

    this.handleDelete = this.handleDelete.bind(this)
    this.toggleEditing = this._toggleEditing.bind(this)
    this.onApply = this._onApply.bind(this)
  }

  _toggleEditing() {
    this.setState({editing: this.props.entity._links.self.href})
  }

  _toggleEditingOff() {
    this.setState({editing: null})
  }

  _onApply(entity) {
    this.props.onUpdate(entity)
    this._toggleEditingOff()
  }


  handleDelete(event) {
    event.stopPropagation()
    this.props.onDelete(this.props.entity)
  }

  renderProperties(sequence) {
    return <div>{JSON.stringify(sequence.properties)}</div>
  }

  getExtraEditEntityArguments() {
    return {}
  }

  render() {
    var sequence = this.props.entity
    if (this.state.editing) {
      return (

        // Edit entity
        // TODO : remove schema from component properties if redundant

        <EditEntity entity={sequence}
          schema={this.props.schema} performance={this.props.performance}
          clazz={this.props.clazz} fields={this.props.fields}
          onApply={this.onApply}
          {...this.getExtraEditEntityArguments()}
        />
      )
    } else {
      return (
        // View sequence

        <div className="row" onClick={this.toggleEditing}>
          <div className="col-1 property-name">{sequence.name}</div>
          <div className="col-3">{this.renderProperties(sequence)}</div>
          <div className="col-2 component">
            {sequence.tick ?
              <Tick
                numerator={sequence.tick.numerator}
                denominator={sequence.tick.denominator}
                timeUnit={sequence.tick.timeUnit}/>
              : <div className="warn">NULL tick</div>
            }
          </div>
          <div className="col-1 property-offset">{sequence.offset}</div>
          <div className="col-1 property-length">{sequence.length}</div>
          <div className="col-1 property-channel">{sequence.channel}</div>
          <div className="col-2">
            {sequence.type}
          </div>
          <div className="col-1">
            <button type="submit" className="btn btn-primary" onClick={this.handleDelete}>Delete</button>
          </div>
        </div>
      )
    }
  }
}

Sequence.propTypes = {
  clazz: PropTypes.object.isRequired,
  entity: PropTypes.object.isRequired,
  fields: PropTypes.object.isRequired,
  onDelete: PropTypes.func.isRequired,
  onUpdate: PropTypes.func.isRequired,
  path: PropTypes.string.isRequired,
  performance: PropTypes.object.isRequired,
  schema: PropTypes.object.isRequired
}

Sequence.defaultProps = {
  path: 'sequence',
  fields: {
    'name': {size: 8, maxLength: 8, defaultValue: ''},
    'properties': {cellWidth: 3},
    'tick': {
      cellWidth: 2,
      fields: {
        'numerator': {defaultValue: 1, cellWidth: 6},
        'denominator': {defaultValue: 1, cellWidth: 6, label: '/'},
        'timeUnit': {defaultValue: 'BEAT', hidden: true}
      }
    },
    'offset': {defaultValue: 0},
    'length': {defaultValue: -1},
    'channel': {defaultValue: 1},
    'type': {
      cellWidth: 2,
      defaultValue: 'pattern'
    },
    'enabled': {
      defaultValue: true,
      hidden: true
    },
    'layers': {
      defaultValue: '',
      hidden: true
    }
  }
}

export default Sequence