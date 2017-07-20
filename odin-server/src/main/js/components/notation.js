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

const crud = require('./../crud');

const EditEntity = require('./editEntity');
const Tick = require('./tick');

// Notation component.
class Notation extends React.Component{
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

  handleDelete() {
    this.props.onDelete(this.props.entity);
  }

  render() {
    var sequence = this.state.entity;
    if (this.state.editing) {
      return (

        // Edit entity
        <EditEntity entity={sequence}
          project={this.props.project}
          path={this.props.path} fields={this.props.fields}
          onApply={this.onUpdate}
        />
      );
    } else {
      return (

        // View entity

        <div className="row" onClick={this.toggleEditing}>
          <div className="col-1">{sequence.channel}</div>
          <div className="col-1">{sequence.offset}</div>
          <div className="col-1">{sequence.length}</div>
          <div className="col-2">
            {sequence.tick ?
              <Tick
                numerator={sequence.tick.numerator}
                denominator={sequence.tick.denominator}
                timeUnit={sequence.tick.timeUnit}/>
              : <div className="warn">NULL tick</div>
            }</div>
          <div className="col-3">{sequence.notation}</div>
          <div className="col-3">
            {sequence.flowName}
          </div>
          <div className="col-1">
            <button onClick={this.handleDelete}>Delete</button>
          </div>
        </div>
      );
    }
  }
}

Notation.defaultProps = {
  path: 'notations',
  fields: {
    'channel' : { defaultValue : 1},
    'offset' : { defaultValue : 0},
    'length' : {defaultValue : -1},
    'tick' : {
      cellWidth : 2,
      fields : {
        'numerator' : {defaultValue : 1},
        'denominator' : {defaultValue : 1},
        'timeUnit' : {defaultValue : 'BEAT'},
      }
    },
    'notation' : {
      cellWidth : 2,
      defaultValue : 'a',
    },
    'flowName' : {defaultValue : 'com.purplepip.odin.music.flow.NotationFlow'}
  },
};

module.exports = Notation;