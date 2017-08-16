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
const ReactDOM = require('react-dom');
import { BrowserRouter as Router, Route } from 'react-router-dom';

const client = require('./client');
const crud = require('./crud');

const ChannelList = require('./components/channelList');
const SequenceList = require('./components/sequenceList');
const Composer = require('./components/composer/composer');

class App extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      projects: [],  project: null, pageSize: 10,
    };

    this.renderSequenceList = this.renderSequenceList.bind(this);
    this.renderChannelList = this.renderChannelList.bind(this);
    this.renderLayerList = this.renderLayerList.bind(this);
  }

  componentDidMount() {
    // Load projects
    client({method: 'GET', path: '/api/projects'}).done(response => {
      var projects = response.entity._embedded.projects;
      this.setState({projects: projects, project: projects[0]});
      crud.loadSchema('channels');
    });
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

  renderChannelList() {
    return (
      <div>
        {this.state.project &&
          <ChannelList project={this.state.project}/>
        }
      </div>
    );
  }

  renderLayerList() {
    return (
      <div>
        {this.state.project &&
            <Composer project={this.state.project}/>
        }
      </div>
    );
  }

  render() {
    return (
      <Router>
        <div>
          <Route exact path="/app" component={this.renderLayerList}/>
          <Route exact path="/app/sequences" component={this.renderSequenceList}/>
          <Route exact path="/app/channels" component={this.renderChannelList}/>
        </div>
      </Router>
    );
  }
}

ReactDOM.render(
  <App />,
  document.getElementById('react')
);
