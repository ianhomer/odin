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
import { BrowserRouter as Router, Route } from 'react-router-dom';

const client = require('./../client');
const crud = require('./../crud');

const ChannelList = require('./channelList');
const SequenceList = require('./sequenceList');
const Composer = require('./composer/composer');

class Project extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      project: null
    };

    this.renderSequenceList = this.renderSequenceList.bind(this);
    this.renderChannelList = this.renderChannelList.bind(this);
    this.renderComposer = this.renderComposer.bind(this);
  }

  componentDidMount() {
    // Load first project (only one project supported for now)
    client({method: 'GET', path: '/api/project'}).done(response => {
      var projects = response.entity._embedded.project;
      this.setState({project: projects[0]});
      crud.loadSchema('channel');
    });
  }

  renderChannelList() {
    return (
      <div>
        {this.state.project &&
          <ChannelList project={this.state.project}/>
        }
      </div>
    );
  }

  renderComposer() {
    return (
      <div>
        {this.state.project &&
          <Composer project={this.state.project}/>
        }
      </div>
    );
  }

  renderSequenceList() {
    return (
      <div>
        {this.state.project &&
          <SequenceList project={this.state.project}/>
        }
      </div>
    );
  }

  render() {
    return (
      <Router>
        <div>
          <Route exact path="/app" component={this.renderComposer}/>
          <Route exact path="/app/sequences" component={this.renderSequenceList}/>
          <Route exact path="/app/channels" component={this.renderChannelList}/>
        </div>
      </Router>
    );
  }
}

Project.contextTypes = {
  schema: PropTypes.object
};

module.exports = Project;