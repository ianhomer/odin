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
const PropTypes = require('prop-types');

const crud = require('./../crud');

const EditEntity = require('./editEntity');
const Note = require('./note');
const Tick = require('./tick');

// Pattern component.
class Pattern extends React.Component{
  constructor(props) {
    super(props);

    this.state = {
      entity: this.props.entity, editing: null
    };

    this.handleDelete = this.handleDelete.bind(this);
    this.toggleEditing = this._toggleEditing.bind(this);
    this.onUpdate = crud.onUpdate.bind(this);
  }

  _toggleEditing() {
    this.setState({editing : this.state.entity._links.self.href});
  }

  handleDelete(event) {
    event.stopPropagation();
    this.props.onDelete(this.props.entity);
  }

  render() {
    var sequence = this.state.entity;
    if (this.state.editing) {
      return (

        // Edit entity

        <EditEntity entity={sequence}
          project={this.props.project}
          clazz={this.props.clazz} fields={this.props.fields} schema={this.props.schema}
          onApply={this.onUpdate}
        />
      );
    } else {
      return (

        // View entity

        <div className="row" onClick={this.toggleEditing}>
          <div className="col-1">{sequence.name}</div>
          <div className="col-1">{sequence.properties.bits}</div>
          <div className="col-2 component">{sequence.properties ?
            <Note
              number={sequence.properties['note.number']}
              velocity={sequence.properties['note.velocity']}
              numerator={sequence.properties['note.numerator']}/>
            : <div className="warn">NULL note</div>
          }</div>
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
      );
    }
  }
}

Pattern.propTypes = {
  clazz: PropTypes.object.isRequired
};

Pattern.defaultProps = {
  path: 'pattern',
  fields: {
    'name' : { size : 8, maxLength : 8},
    'bits' : { defaultValue : 1 },
    'note' : {
      cellWidth : 2,
      fields : {
        'number' : {defaultValue : 60, cellWidth : 4, maxLength : 3},
        'velocity' : {defaultValue : 100, cellWidth : 4, maxLength : 3},
        'numerator' : {defaultValue : 1, cellWidth : 4},
        'denominator' : {defaultValue : 1, hidden : true}
      }
    },
    'tick' : {
      cellWidth : 2,
      fields : {
        'numerator' : {defaultValue : 1, cellWidth : 6},
        'denominator' : {defaultValue : 1, cellWidth : 6, label : '/'},
        'timeUnit' : {defaultValue : 'BEAT', hidden : true},
      }
    },
    'offset' : { defaultValue : 0 },
    'length' : { defaultValue : -1 },
    'channel' : { defaultValue : 1 },
    'flowName' : {
      cellWidth : 2,
      defaultValue : 'pattern',
      readOnly : true
    },
    'layers' : {
      defaultValue : '',
      hidden : true
    }
  },
};

module.exports = Pattern;