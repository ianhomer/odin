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

const Notation = require('./notation');
//const Pattern = require('./pattern');
const Trace = require('./trace');

// Rendering of generic sequence list
class SequenceList extends React.Component{
  constructor(props) {
    super(props);
    this.state = {
      schema: [], entities: [], links: []
    };

    crud.bindMe(this);
  }

  componentDidMount() {
    crud.loadSchema('notations').then(() => {
      this.loadFromServer();
    });
  }

  render() {
    var entities = this.state.entities.map(entity =>
      <div key={'div-' + entity._links.self.href}>
        {'notation' in entity._links &&
          <Notation entity={entity} key={entity._links.self.href}
            project={this.props.project}
            path={this.props.path} fields={this.props.fields} schema={this.state.schema}
            onDelete={this.onDelete} onUpdate={this.onUpdate}
          />
        }
      </div>
    );
    return (
      // View sequence list.
      <div>
        <Trace scope={this.props.path}/>
        <div className="container">
          <div className="row">
            <div className="col-1">Channel</div>
            <div className="col-1">Offset</div>
            <div className="col-1">Length</div>
            <div className="col-2">Tick</div>
            <div className="col-3">Configuration</div>
            <div className="col-3">Flow Name</div>
          </div>
          {entities}
        </div>
      </div>
    );
  }
}

SequenceList.defaultProps = {
  path: 'sequences'
};

module.exports = SequenceList;