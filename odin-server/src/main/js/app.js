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
const client = require('./client');
const crud = require('./crud');

const ChannelList = require('./components/channelList');
const LayerList = require('./components/layerList');
const ProjectList = require('./components/projectList');
const SequenceList = require('./components/sequenceList');

class App extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      sequences: [], projects: [],  project: null, pageSize: 10,
    };
  }

  componentDidMount() {
    // Load projects
    client({method: 'GET', path: '/api/projects'}).done(response => {
      var projects = response.entity._embedded.projects;
      this.setState({projects: projects, project: projects[0]});

      crud.loadSchema('channels');
    });
  }

  render() {
    // TODO : Switch to generic sequence list to configure more than just patterns
    return (
      <div>
        {this.state.entities &&
          <div className="warn">WARNING : Entities store in app state {JSON.stringify(this.state.entities)}</div>
        }
        <ProjectList projects={this.state.projects}/>
        {this.state.project &&
          <div>
            <SequenceList project={this.state.project}/>
          </div>
        }
        {this.state.project &&
          <LayerList project={this.state.project}/>
        }
        {this.state.project &&
          <ChannelList project={this.state.project}/>
        }
      </div>
    );
  }
}

ReactDOM.render(
  <App />,
  document.getElementById('react')
);
