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
const PropTypes = require('prop-types');

import { BrowserRouter as Router, Route } from 'react-router-dom';
import { Schema } from './schema/schema';

const client = require('./client');

const Project = require('./components/project');
const Developer = require('./components/developer/developer');

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      schema : null
    };

    this.renderProject = this.renderProject.bind(this);
    this.renderDeveloper = this.renderDeveloper.bind(this);
  }

  getChildContext() {
    return {schema: this.state.schema};
  }

  componentDidMount() {
    client({method: 'GET', path: '/services/schema'}).done(response => {
      this.setState({schema: new Schema(response.entity)});
    });
  }

  renderProject() {
    return (
      <div>
        {this.state.schema &&
          <Project/>
        }
      </div>
    );
  }

  renderDeveloper() {
    return (
      <div>
        {this.state.schema &&
          <Developer project={this.state.project}/>
        }
      </div>
    );
  }

  render() {
    return (
      <Router>
        <div>
          <Route exact path="/app/developer" component={this.renderDeveloper}/>
          <Route path="/app" component={this.renderProject}/>
        </div>
      </Router>
    );
  }
}

App.childContextTypes = {
  schema: PropTypes.object
};

ReactDOM.render(
  <App />,
  document.getElementById('react')
);
