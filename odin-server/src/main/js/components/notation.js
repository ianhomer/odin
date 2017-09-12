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

const EditEntity = require('./editEntity')
const Tick = require('./tick')
const Score = require('./score')

// Notation component.
class Notation extends React.Component{
  constructor(props) {
    super(props)

    this.state = {
      entity: this.props.entity, editing: null
    }

    this.handleDelete = this.handleDelete.bind(this)
    this.toggleEditing = this._toggleEditing.bind(this)
    this.onUpdate = this.props.flux.onUpdate.bind(this)
  }

  _toggleEditing() {
    this.setState({editing : this.state.entity._links.self.href})
  }

  handleDelete(event) {
    event.stopPropagation()
    this.props.onDelete(this.props.entity)
  }

  render() {
    var sequence = this.state.entity
    if (this.state.editing) {
      return (

        // Edit entity
        <EditEntity entity={sequence}
          schema={this.props.schema} project={this.props.project} flux={this.props.flux}
          clazz={this.props.clazz} fields={Notation.defaultProps.fields}
          onApply={this.onUpdate}
        />
      )
    } else {
      return (

        // View entity

        <div className="row" onClick={this.toggleEditing}>
          <div className="col-1">{sequence.name}</div>
          <div className="col-3">
            <Score entity={sequence} displayText="true" width="800" flux={this.props.flux}/>
          </div>
          <div className="col-2 component">
            {sequence.tick ?
              <Tick
                numerator={sequence.tick.numerator}
                denominator={sequence.tick.denominator}
                timeUnit={sequence.tick.timeUnit}/>
              : <div className="warn">NULL tick</div>
            }
          </div>
          <div className="col-1">{sequence.offset}</div>
          <div className="col-1">{sequence.length}</div>
          <div className="col-1">{sequence.channel}</div>
          <div className="col-2">
            {sequence.flowName}
          </div>
          <div className="col-1">
            <button type="submit" className="btn btn-primary" onClick={this.handleDelete}>Delete</button>
          </div>
        </div>
      )
    }
  }
}

Notation.propTypes = {
  clazz: PropTypes.object.isRequired,
  flux: PropTypes.object.isRequired,
  schema: PropTypes.object.isRequired
}

Notation.defaultProps = {
  path: 'notation',
  fields: {
    'name' : { size : 8, maxLength : 8},
    'format' : {
      cellWidth : 1,
      defaultValue : 'natural',
      hidden : true
    },
    'notation' : {
      cellWidth : 3,
      defaultValue : 'C',
    },
    'tick' : {
      cellWidth : 2,
      fields : {
        'numerator' : {defaultValue : 1, cellWidth : 6},
        'denominator' : {defaultValue : 1, cellWidth : 6, label : '/'},
        'timeUnit' : {defaultValue : 'BEAT', hidden : true},
      }
    },
    'offset' : { defaultValue : 0},
    'length' : {defaultValue : -1},
    'channel' : { defaultValue : 1},
    'flowName' : {
      cellWidth : 2,
      defaultValue : 'notation',
      readOnly : true
    },
    'layers' : {
      defaultValue : '',
      hidden : true
    }
  },
}

module.exports = Notation