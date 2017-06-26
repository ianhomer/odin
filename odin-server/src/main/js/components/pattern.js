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

/*eslint-env node*/

'use strict';

const React = require('react');

const EditEntity = require('./editEntity');
const Note = require('./note');
const Tick = require('./tick');

// Pattern component.
class Pattern extends React.Component{
  constructor(props) {
    super(props);

    this.state = {};

    this.handleDelete = this.handleDelete.bind(this);
    this.toggleEditing = this._toggleEditing.bind(this);
    this.onApplySuccess = this._onApplySuccess.bind(this);
  }

  _toggleEditing(e) {
    console.log('Editing : ' + this.props.entity._links.self.href);
    this.setState({editing : this.props.entity._links.self.href});
  }

  _onApplySuccess(e) {
    console.log('On apply success');
    this.setState({editing : null});
  }

  handleDelete() {
    this.props.onDelete(this.props.entity);
  }

  render() {
    var sequence = this.props.entity;
    if (this.state.editing) {
      return (

        // Edit entity

        <EditEntity entity={sequence}
          project={this.props.project}
          path={this.props.path} fields={this.props.fields} schema={this.props.schema}
          onApply={this.onApplySuccess}
          onApplySuccess={this.onApplySuccess}
        />
      );
    } else {
      return (

        // View entity

        <div className="row" onClick={this.toggleEditing}>
          <div className="col-1">{sequence.channel}</div>
          <div className="col-1">{sequence.offset}</div>
          <div className="col-1">{sequence.length}</div>
          <div className="col-1">{sequence.bits}</div>
          <div className="col-2">
            {sequence.tick ?
              <Tick
                numerator={sequence.tick.numerator}
                denominator={sequence.tick.denominator}
                timeUnit={sequence.tick.timeUnit}/>
              : <div className="warn">NULL tick</div>
            }</div>
          <div className="col-2">{sequence.note ?
            <Note
              number={sequence.note.number}
              velocity={sequence.note.velocity}
              duration={sequence.note.duration}/>
            : <div className="warn">NULL note</div>
          }</div>
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

module.exports = Pattern;