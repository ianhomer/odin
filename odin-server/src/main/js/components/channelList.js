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

const Trace = require('./trace');
const Channel = require('./channel');
const EditEntity = require('./editEntity');

class ChannelList extends React.Component{
  constructor(props) {
    super(props);
    this.state = {
      entities: [], links: [], loaded: false
    };

    crud.bindMe(this);
  }

  componentDidMount() {
    this.loadFromServer();
  }

  render() {
    var entities = this.state.entities.map(entity =>
      <Channel entity={entity} key={entity._links.self.href}
        path={this.props.path}
        onDelete={this.onDelete}/>
    );

    return (
      <div>
        <Trace scope="channelList"/>
        <div className="container">
          <div className="row">
            <div className="col-2">Number</div>
            <div className="col-3">Program</div>
          </div>
          {this.isSchemaLoaded() &&
            <EditEntity
              project={this.props.project}
              path={this.props.path} fields={this.props.fields}
              onApply={this.onCreate}
            />
          }
          {entities}
        </div>
      </div>
    );
  }
}

ChannelList.defaultProps = {
  path: 'channels',
  fields: {
    'number' : {
      defaultValue : '1',
      cellWidth : 2
    },
    'programName' : {
      defaultValue : 'piano',
      cellWidth : 3
    }
  }
};

module.exports = ChannelList;